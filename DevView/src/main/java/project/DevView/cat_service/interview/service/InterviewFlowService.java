// src/main/java/project/DevView/cat_service/interview/service/InterviewFlowService.java
package project.DevView.cat_service.interview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.DevView.cat_service.ai.service.ChatGptService;
import project.DevView.cat_service.global.exception.CustomException;
import project.DevView.cat_service.global.exception.ErrorCode;
import project.DevView.cat_service.interview.entity.Interview;
import project.DevView.cat_service.interview.entity.InterviewMessage;
import project.DevView.cat_service.interview.mapper.InterviewMessageMapper;
import project.DevView.cat_service.interview.repository.InterviewMessageRepository;
import project.DevView.cat_service.interview.repository.InterviewRepository;
import project.DevView.cat_service.question.entity.Question;
import project.DevView.cat_service.question.entity.UserQuestionHistory;
import project.DevView.cat_service.question.repository.QuestionRepository;
import project.DevView.cat_service.question.repository.UserQuestionHistoryRepository;
import project.DevView.cat_service.user.repository.UserRepository;
import project.DevView.cat_service.resume.entity.Resume;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewFlowService {

    private final InterviewRepository            interviewRepository;
    private final QuestionRepository             questionRepository;
    private final InterviewMessageRepository     messageRepository;
    private final UserQuestionHistoryRepository  historyRepository;
    private final UserRepository                 userRepository;
    private final ChatGptService                 chatGptService;

    /**
     * (1) 아직 답변 안 한 질문 한 개 꺼내기
     */
    public Question getNextQuestion(long userId, Long interviewId) {
        Interview iv = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_EXIST));

        List<Long> used = historyRepository.findAnsweredQuestionIds(userId);
        Optional<Question> q = used.isEmpty()
                ? questionRepository.findOneWithoutUsedIds(iv.getField().getName())
                : questionRepository.findOneWithUsedIds(iv.getField().getName(), used);

        // 질문을 찾았다면 UserQuestionHistory 생성 및 완료 처리
        q.ifPresent(question -> {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
            
            // UserQuestionHistory 생성
            var history = UserQuestionHistory.builder()
                    .user(user)
                    .question(question)
                    .answeredAt(LocalDateTime.now())
                    .completed(true)
                    .build();
            
            historyRepository.save(history);
        });

        return q.orElse(null);
    }

    /**
     * (2) AI 질문 메시지 저장
     */
    public InterviewMessage createQuestionMessage(Long interviewId, Question q) {
        Interview iv = loadInterview(interviewId);
        InterviewMessage msg = InterviewMessageMapper.questionToMessage(iv, q);
        // ↓ setCreatedAt() 지우고 Auditing에 맡긴다
        return messageRepository.save(msg);
    }

    /**
     * (3) 사용자 답변 메시지 저장
     */
    public InterviewMessage createAnswerMessage(Long interviewId, String answer) {
        Interview iv = loadInterview(interviewId);
        InterviewMessage msg = InterviewMessageMapper.answerToMessage(iv, answer);
        return messageRepository.save(msg);
    }

    /**
     * (4) ChatGPT로 꼬리질문 생성 & 저장
     */
    public InterviewMessage createFollowUpQuestion(Long interviewId, String answer) {
        Interview iv = loadInterview(interviewId);
        String context = buildContext(interviewId);
        String followUp = chatGptService.generateFollowUpQuestion(context, answer);
        InterviewMessage msg = InterviewMessageMapper.followupToMessage(iv, followUp);
        return messageRepository.save(msg);
    }

    /**
     * (5) 전체 메시지 리스트
     */
    public List<InterviewMessage> getMessages(Long interviewId) {
        return messageRepository.findByInterview_IdOrderByCreatedAtAsc(interviewId);
    }

    /**
     * (6) 현재 꼬리질문 종료 처리
     */
    public void finishCurrentQuestion(Long interviewId, long userId) {
        Interview iv = loadInterview(interviewId);
        // primitive int 비교
        if (iv.getUser().getId() != userId) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        // question 필드가 제거되었으므로 이 메서드는 더 이상 필요하지 않습니다.
        // 대신 인터뷰 종료 시점에 모든 질문을 완료 처리하는 방식으로 변경할 수 있습니다.
    }

    @Transactional(readOnly = true)
    public String evaluateMessages(Long interviewId) {
        // 1. 메시지 조회
        List<InterviewMessage> messages = messageRepository.findByInterview_IdOrderByCreatedAtAsc(interviewId);
        
        // 2. 채팅 히스토리 형식으로 변환
        String chatHistory = messages.stream()
            .map(message -> String.format("%s : %s", 
                message.getMessageType().equals("QUESTION") ? "질문" : "답변",
                message.getContent()))
            .collect(Collectors.joining("\n"));

        // 3. AI 평가 프롬프트 생성
        String prompt = String.format("""
            당신은 컴퓨터공학 기술 면접관입니다. 아래는 지금까지의 질문, 답변 결과입니다.

            %s

            이 데이터를 바탕으로, 이 지원자에 대한 종합 평가를 작성하세요.  
            다음 기준에 따라 이 답변을 1~5점으로 평가하고, 각 항목별로 간단한 이유를 작성하세요:

            1. 정확성 : 기술한 내용이 사실과 부합하는가? 핵심 개념을 정확히 설명했는가?
            2. 완전성 : 질문에 요구되는 설명이 충분한가? 중요한 개념이 빠지지 않았는가?
            3. 표현력 : 명확하고 이해하기 쉽게 설명했는가? 용어 사용이 적절한가?
            4. 간결성 : 응답 길이가 말하기 기준 1분 이내로 적절한가?

            ---
            또한 아래 항목을 포함해야 합니다:

            1. 총점 (100점 만점) – 항목별 평균 점수 기반  
            2. 강점 – 어떤 역량이나 자세가 특히 뛰어났는가?  
            3. 개선점 – 반복적으로 아쉬웠던 부분은 무엇인가?  
            4. 추천 준비 방향 – 다음 면접을 위한 구체적인 조언  
            5. 답변 스타일 및 태도 관련 종합 피드백 – 커뮤니케이션 및 태도 중심 피드백
            """, chatHistory);

        // 4. AI 평가 요청
        return chatGptService.getCompletion(prompt);
    }

    /* internal */
    private Interview loadInterview(Long id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_EXIST));
    }

    private String buildContext(Long interviewId) {
        var msgs = messageRepository.findByInterview_IdOrderByCreatedAtAsc(interviewId);
        var sb = new StringBuilder();
        msgs.forEach(m -> sb.append("[").append(m.getSender()).append("] ")
                .append(m.getContent()).append("\n"));
        return sb.toString();
    }
}
