package project.DevView.cat_service.resume.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.DevView.cat_service.resume.dto.ResumeRequest;
import project.DevView.cat_service.resume.dto.ResumeResponse;
import project.DevView.cat_service.resume.entity.Resume;
import project.DevView.cat_service.resume.entity.ResumeMessage;
import project.DevView.cat_service.resume.repository.ResumeRepository;
import project.DevView.cat_service.user.entity.UserEntity;
import project.DevView.cat_service.user.repository.UserRepository;
import project.DevView.cat_service.interview.service.InterviewFlowService;
import project.DevView.cat_service.resume.service.ResumeMessageService;
import project.DevView.cat_service.ai.service.ChatGptService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final InterviewFlowService interviewFlowService;
    private final ResumeMessageService resumeMessageService;
    private final ChatGptService chatGptService;

    @Transactional
    public ResumeResponse createResume(Long userId, ResumeRequest request) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Resume resume = Resume.builder()
            .user(user)
            .content(request.getContent())
            .build();

        return ResumeResponse.from(resumeRepository.save(resume));
    }

    @Transactional(readOnly = true)
    public ResumeResponse getResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        return ResumeResponse.from(resume);
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> getUserResumes(Long userId) {
        return resumeRepository.findByUserId(userId).stream()
            .map(ResumeResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public ResumeResponse updateResume(Long resumeId, Long userId, ResumeRequest request) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        if (!resume.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Resume does not belong to the user");
        }

        resume.setContent(request.getContent());
        return ResumeResponse.from(resume);
    }

    @Transactional
    public void deleteResume(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        if (!resume.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Resume does not belong to the user");
        }

        resumeRepository.delete(resume);
    }

    @Transactional
    public String createFollowUpQuestion(Long resumeId, String answer) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        
        // 1. 먼저 사용자의 답변을 저장
        resumeMessageService.saveAnswer(resumeId, answer);
        
        // 2. AI를 통해 꼬리 질문 생성
        String followUpQuestion = interviewFlowService.createFollowUpQuestionForResume(resume.getContent(), answer);
        
        // 3. 생성된 꼬리 질문을 저장
        resumeMessageService.saveQuestion(resumeId, followUpQuestion);
        
        // 4. 생성된 꼬리 질문 반환
        return followUpQuestion;
    }

    @Transactional(readOnly = true)
    public List<ResumeMessage> getMessages(Long resumeId) {
        return resumeMessageService.getMessages(resumeId);
    }

    @Transactional(readOnly = true)
    public String evaluateMessages(Long resumeId) {
        // 1. 메시지 조회
        List<ResumeMessage> messages = resumeMessageService.getMessages(resumeId);
        
        // 2. 채팅 히스토리 형식으로 변환
        String chatHistory = messages.stream()
            .map(message -> String.format("%s : %s", 
                message.getType() == ResumeMessage.MessageType.QUESTION ? "질문" : "답변",
                message.getContent()))
            .collect(Collectors.joining("\n"));

        // 3. AI 평가 프롬프트 생성
        String prompt = String.format("""
            당신은 자기소개서 기반의 프로젝트 경험 면접을 진행한 기술 면접관입니다. 아래는 지금까지의 질문, 답변 결과입니다.
           
            %s
            이 데이터를 바탕으로, 이 지원자에 대한 종합 평가를 작성하세요. \s
            다음 기준에 따라 이 답변을 1~5점으로 평가하고, 각 항목별로 간단한 이유를 작성하세요:

            1. 질문의 의도 파악 : 질문의 핵심을 정확히 이해하고 맞춤형으로 답했는가? \s
            2. 답변의 구체성 : 개념이 아닌 실제 경험과 수치를 들어 상세히 설명했는가? \s
            3. 사유와 결과의 일관성 : 문제의 원인, 대응, 결과의 흐름이 일관적인가? \s
            4. 예외 상황에 대한 대응성 : 예상치 못한 상황에 대한 설명이나 대응책이 포함되었는가? \s
            5. 답변/설명의 논리적 구성 : 말의 순서, 구성, 전개 방식이 논리적인가?
            4. 답변의 간결성 : 응답 길이가 말하기 기준 1분 이내로 적절한가?
            ---
            또한 아래 항목을 포함해야 합니다:
            1. 총점 (100점 만점) – 항목별 평균 점수 기반 \s
            2. 강점 – 어떤 역량이나 자세가 특히 뛰어났는가? \s
            3. 개선점 – 반복적으로 아쉬웠던 부분은 무엇인가? \s
            4. 추천 준비 방향 – 다음 면접을 위한 구체적인 조언 \s
            5. 답변 스타일 및 태도 관련 종합 피드백 – 커뮤니케이션 및 태도 중심 피드백
            """, chatHistory);

        // 4. AI 평가 요청
        return chatGptService.getCompletion(prompt);
    }
} 