// src/main/java/project/DevView/cat_service/interview/service/InterviewFlowService.java
package project.DevView.cat_service.interview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.DevView.cat_service.global.exception.CustomException;
import project.DevView.cat_service.global.exception.ErrorCode;
import project.DevView.cat_service.interview.entity.Interview;
import project.DevView.cat_service.interview.entity.InterviewMessage;
import project.DevView.cat_service.interview.mapper.InterviewMessageMapper;
import project.DevView.cat_service.interview.repository.InterviewMessageRepository;
import project.DevView.cat_service.interview.repository.InterviewRepository;
import project.DevView.cat_service.question.entity.Question;
import project.DevView.cat_service.question.repository.QuestionRepository;
import project.DevView.cat_service.question.repository.UserQuestionHistoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewFlowService {

    private final InterviewRepository            interviewRepository;
    private final QuestionRepository             questionRepository;
    private final InterviewMessageRepository     messageRepository;
    private final UserQuestionHistoryRepository  historyRepository;
    private final ChatGptService                 chatGptService;

    /**
     * (1) 아직 답변 안 한 질문 한 개 꺼내기
     */
    public Question getNextQuestion(int userId, Long interviewId) {
        Interview iv = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_EXIST));

        List<Long> used = historyRepository.findAnsweredQuestionIds(userId);
        Optional<Question> q = used.isEmpty()
                ? questionRepository.findOneWithoutUsedIds(iv.getField().getName())
                : questionRepository.findOneWithUsedIds(iv.getField().getName(), used);

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
    public void finishCurrentQuestion(Long interviewId, int userId) {
        Interview iv = loadInterview(interviewId);
        // primitive int 비교
        if (iv.getUser().getId() != userId) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        messageRepository
                .findLastQuestionMessage(interviewId)
                .ifPresent(m -> historyRepository.markQuestionCompleted(
                        userId, m.getQuestion().getId()
                ));
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
