package com.example.cat_service.interview.service;

import com.example.cat_service.interview.dto.InterviewResponseDto;
import com.example.cat_service.interview.entity.Field;
import com.example.cat_service.interview.entity.Interview;
import com.example.cat_service.interview.entity.InterviewMessage;
import com.example.cat_service.interview.entity.Question;
import com.example.cat_service.interview.repository.InterviewMessageRepository;
import com.example.cat_service.interview.repository.InterviewRepository;
import com.example.cat_service.interview.repository.QuestionRepository;
import com.example.cat_service.interview.repository.UserQuestionHistoryRepository;
import com.example.cat_service.user.entity.UserEntity;
import com.example.cat_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 기존 InterviewService + 인터뷰 플로우(질문/답변/꼬리질문) 관련 메서드를 모두 통합한 예시
 */
@Service
@RequiredArgsConstructor
public class InterviewService {

    // ==============================
    // 기존 필드
    // ==============================
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

    // ==============================
    // 새 로직(플로우)에서 필요한 Repository
    // ==============================
    private final QuestionRepository questionRepository;
    private final InterviewMessageRepository interviewMessageRepository;
    private final UserQuestionHistoryRepository userQuestionHistoryRepository;



    private final ChatGptService chatGptService;


    // ==============================
    // [기존] 인터뷰 생성 (DTO 반환)
    // ==============================
    public InterviewResponseDto createInterview(int userId, Field field) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found. id=" + userId));

        Interview interview = new Interview();
        interview.setUser(user);
        interview.setField(field);
        interview.setStartedAt(LocalDateTime.now());
        // Auditing (createdAt, createdBy 등) 원하는 대로 세팅 가능

        Interview saved = interviewRepository.save(interview);
        return convertToDto(saved);
    }

    // ==============================
    // [추가] 인터뷰 생성 (Interview 엔티티 직접 반환) - Flow 예시용
    // ==============================
    public Interview createInterviewEntity(int userId, Field field) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found. id=" + userId));

        Interview interview = new Interview();
        interview.setUser(user);
        interview.setField(field);
        interview.setStartedAt(LocalDateTime.now());

        return interviewRepository.save(interview);
    }

    // ==============================
    // [기존] 분야별 인터뷰 목록 조회
    // ==============================
    public List<InterviewResponseDto> findByUserAndField(int userId, Field field) {
        List<Interview> interviews = interviewRepository.findByUserIdAndField(userId, field);
        return interviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ==============================
    // [기존] 인터뷰 상세 조회
    // ==============================
    public InterviewResponseDto getInterviewDetail(Long interviewId, int userId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found. id=" + interviewId));

        // 권한 체크
        if (interview.getUser().getId() != userId) {
            throw new RuntimeException("Not allowed to access this interview.");
        }

        return convertToDto(interview);
    }

    // ==============================
    // [기존] 인터뷰 종료
    // ==============================
    public void finishInterview(Long interviewId, int userId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found. id=" + interviewId));

        // 권한 체크
        if (interview.getUser().getId() != userId) {
            throw new RuntimeException("Not allowed to access this interview.");
        }

        interview.setEndedAt(LocalDateTime.now());
        interviewRepository.save(interview);
    }

    // ==============================
    // [새로 추가] 아직 답변 안 한 Question 중 하나 찾기
    // ==============================
    public Question findNextQuestionToAsk(int userId, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found. id=" + interviewId));

        // 이미 답변한 Question ID 목록
        List<Long> usedQuestionIds = userQuestionHistoryRepository.findAnsweredQuestionIds(userId);
        System.out.println("[DEBUG] usedQuestionIds = " + usedQuestionIds);

        String fieldName = interview.getField().getName();
        System.out.println("[DEBUG] fieldName = " + fieldName);

        Optional<Question> result;
        if (usedQuestionIds.isEmpty()) {
            System.out.println("[DEBUG] usedIds empty => use findOneWithoutUsedIds");
            result = questionRepository.findOneWithoutUsedIds(fieldName);
        } else {
            System.out.println("[DEBUG] usedIds size=" + usedQuestionIds.size() + " => use findOneWithUsedIds");
            result = questionRepository.findOneWithUsedIds(fieldName, usedQuestionIds);
        }

        if (result.isPresent()) {
            System.out.println("[DEBUG] Found question ID = " + result.get().getId());
            return result.get();
        } else {
            System.out.println("[DEBUG] No question found => returning null");
            return null;
        }
    }


    // ==============================
    // [새로 추가] InterviewMessage: 질문 생성
    // ==============================
    public InterviewMessage createQuestionMessage(Long interviewId, Question question) {
        InterviewMessage msg = new InterviewMessage();
        msg.setInterview(interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found:" + interviewId)));
        msg.setSender("AI"); // SYSTEM / AI
        msg.setMessageType("QUESTION");
        msg.setContent(question.getContent());
        msg.setQuestion(question);
        msg.setCreatedAt(LocalDateTime.now());
        // etc ...
        return interviewMessageRepository.save(msg);
    }

    // ==============================
    // [새로 추가] 사용자 답변 메시지 생성
    // ==============================
    public InterviewMessage createAnswerMessage(Long interviewId, String userAnswer) {
        InterviewMessage msg = new InterviewMessage();
        msg.setInterview(interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found:" + interviewId)));
        msg.setSender("USER");
        msg.setMessageType("ANSWER");
        msg.setContent(userAnswer);
        msg.setCreatedAt(LocalDateTime.now());
        // etc ...
        return interviewMessageRepository.save(msg);
    }

    // ==============================
    // [새로 추가] 꼬리질문 생성
    // ==============================
    public InterviewMessage createFollowUpQuestion(Long interviewId, String userAnswer) {
        // 1) 인터뷰 엔티티 조회
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found:" + interviewId));

        // 2) 이전 대화(메시지)들을 간단히 합쳐서 ChatGPT에 넘길 "conversationContext"를 만든다
        //    예시: "Previous Q&A: [AI] 'What is OS?' [USER] 'An operating system...' ... "
        //    실제 프로젝트라면 메시지 목록을 가져와서 summarize하거나 전부 연결
        String conversationContext = buildConversationContext(interviewId);

        // 3) ChatGPT API 호출해 followUp 질문 생성
        //    - userAnswer도 추가로 넣어 "사용자 답변" 반영
        String followUp = chatGptService.generateFollowUpQuestion(conversationContext, userAnswer);

        // 4) DB에 FOLLOWUP_QUESTION 메시지로 저장
        InterviewMessage msg = new InterviewMessage();
        msg.setInterview(interview);
        msg.setSender("AI");
        msg.setMessageType("FOLLOWUP_QUESTION");
        msg.setContent(followUp);
        msg.setCreatedAt(LocalDateTime.now());

        return interviewMessageRepository.save(msg);
    }

    /**
     * 대화 맥락(conversationContext)을 만드는 예시 메서드
     */
    private String buildConversationContext(Long interviewId) {
        // (a) 메시지 목록 조회
        List<InterviewMessage> messages = interviewMessageRepository
                .findByInterview_IdOrderByCreatedAtAsc(interviewId);

        // (b) 간단히: AI/USER 메시지를 연결
        // 예:
        //  "[AI] Hello user\n[USER] I want to talk about OS\n[AI] Sure, OS stands for...\n"
        StringBuilder sb = new StringBuilder("Previous conversation:\n");
        for (InterviewMessage m : messages) {
            sb.append("[").append(m.getSender()).append("] ")
                    .append(m.getContent()).append("\n");
        }
        return sb.toString();
    }


    // ==============================
    // [새로 추가] 메시지 전체 조회
    // ==============================
    public List<InterviewMessage> getMessages(Long interviewId) {
        return interviewMessageRepository.findByInterview_IdOrderByCreatedAtAsc(interviewId);
    }

    // ==============================
    // [새로 추가] 꼬리 질문 종료
    // ==============================
    public void finishCurrentQuestion(Long interviewId, int userId) {
        // 여기서는 "마지막 질문"을 찾아 userQuestionHistory에 completed=true
        // 또는 "이 Question은 완료" 플래그 처리 (스텁 코드)

        // (a) 인터뷰 권한 체크
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found:" + interviewId));
        if (interview.getUser().getId() != userId) {
            throw new RuntimeException("Not allowed to access this interview.");
        }

        // (b) 메시지 중 마지막 QUESTION 가져오기
        InterviewMessage lastQuestionMsg = interviewMessageRepository.findLastQuestionMessage(interviewId)
                .orElse(null);
        if (lastQuestionMsg != null && lastQuestionMsg.getQuestion() != null) {
            // userQuestionHistory에서 completed=true 처리 (실제 로직 필요)
            userQuestionHistoryRepository.markQuestionCompleted(
                    userId,
                    lastQuestionMsg.getQuestion().getId()
            );
        }
    }

    // ==============================
    // 내부 편의 메서드
    // ==============================
    private InterviewResponseDto convertToDto(Interview interview) {
        InterviewResponseDto dto = new InterviewResponseDto();
        dto.setInterviewId(interview.getId());
        dto.setUserId(interview.getUser().getId()); // int
        dto.setFieldName(interview.getField().getName());
        dto.setStartedAt(interview.getStartedAt());
        dto.setEndedAt(interview.getEndedAt());
        return dto;
    }
}
