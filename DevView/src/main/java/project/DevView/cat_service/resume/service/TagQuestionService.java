package project.DevView.cat_service.resume.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.DevView.cat_service.ai.service.ResumeAIService;
import project.DevView.cat_service.resume.dto.TagQuestionResponse;
import project.DevView.cat_service.resume.entity.Resume;
import project.DevView.cat_service.resume.entity.ResumeTag;
import project.DevView.cat_service.resume.entity.TagQuestion;
import project.DevView.cat_service.resume.repository.ResumeRepository;
import project.DevView.cat_service.resume.repository.ResumeTagRepository;
import project.DevView.cat_service.resume.repository.TagQuestionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagQuestionService {

    private final ResumeRepository resumeRepository;
    private final ResumeTagRepository resumeTagRepository;
    private final TagQuestionRepository tagQuestionRepository;
    private final ResumeAIService resumeAIService;

    @Transactional
    public TagQuestionResponse generateQuestionsForResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        // 해당 resume의 태그들 중에서 아직 완료되지 않은 질문이 있는 태그를 찾음
        List<ResumeTag> tags = resumeTagRepository.findByResumeId(resumeId);
        
        // 각 태그별로 완료되지 않은 질문 수를 확인
        for (ResumeTag tag : tags) {
            List<TagQuestion> incompleteQuestions = tagQuestionRepository.findByResumeTagIdAndIsCompletedFalse(tag.getId());
            
            // 해당 태그에 대한 질문이 없거나 모두 완료된 경우에만 새로운 질문 생성
            if (incompleteQuestions.isEmpty()) {
                // AI 서비스를 통해 질문 생성 (단일 태그를 리스트로 감싸서 전달)
                TagQuestionResponse response = resumeAIService.generateQuestions(List.of(tag));
                
                // 생성된 질문들을 저장
                List<TagQuestion> questions = response.getQuestions().stream()
                    .map(question -> TagQuestion.builder()
                        .resumeTag(tag)
                        .baseQuestion(question.getBaseQuestion())
                        .createdQuestion(question.getCreatedQuestion())
                        .isCompleted(false)
                        .build())
                    .collect(Collectors.toList());

                tagQuestionRepository.saveAll(questions);
                return response;
            }
        }

        throw new IllegalStateException("모든 태그에 대한 질문이 이미 생성되어 있습니다.");
    }

    @Transactional
    public void markQuestionAsCompleted(Long resumeId, Long questionId) {
        // resumeId로 해당 질문이 실제로 해당 이력서에 속하는지 확인
        TagQuestion question = tagQuestionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        if (!question.getResumeTag().getResume().getId().equals(resumeId)) {
            throw new IllegalArgumentException("Question does not belong to the specified resume");
        }

        question.setCompleted(true);
    }
} 