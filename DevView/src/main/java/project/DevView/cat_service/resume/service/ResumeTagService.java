package project.DevView.cat_service.resume.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.DevView.cat_service.ai.service.ResumeAIService;
import project.DevView.cat_service.resume.dto.ResumeTagResponse;
import project.DevView.cat_service.resume.dto.TagQuestionResponse;
import project.DevView.cat_service.resume.entity.Resume;
import project.DevView.cat_service.resume.entity.ResumeTag;
import project.DevView.cat_service.resume.entity.TagQuestion;
import project.DevView.cat_service.resume.repository.ResumeRepository;
import project.DevView.cat_service.resume.repository.ResumeTagRepository;
import project.DevView.cat_service.resume.repository.TagQuestionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeTagService {

    private final ResumeRepository resumeRepository;
    private final ResumeTagRepository resumeTagRepository;
    private final TagQuestionRepository tagQuestionRepository;
    private final ResumeAIService resumeAIService;

    @Transactional(readOnly = true)
    public boolean isGenerationCompleted(Long resumeId) {
        log.info("태그 및 질문 생성 완료 여부 확인 - resumeId: {}", resumeId);
        
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다: " + resumeId));

        // 태그가 하나라도 있으면 생성이 완료된 것으로 간주
        return !resumeTagRepository.findByResumeId(resumeId).isEmpty();
    }






    @Transactional(readOnly = true)
    public ResumeTagResponse getTags(Long resumeId) {
        log.info("이력서 태그 조회 - resumeId: {}", resumeId);
        
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다: " + resumeId));

        List<ResumeTag> tags = resumeTagRepository.findByResumeId(resumeId);
        
        return ResumeTagResponse.builder()
            .keywords(tags.stream()
                .map(tag -> ResumeTagResponse.KeywordItem.builder()
                    .tagType(tag.getTagType())
                    .keyword(tag.getKeyword())
                    .detail(tag.getDetail())
                    .depthScore(tag.getDepthScore())
                    .priorityScore(tag.getPriorityScore())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTags(List<ResumeTag> tags) {
        resumeTagRepository.saveAll(tags);
    }
} 