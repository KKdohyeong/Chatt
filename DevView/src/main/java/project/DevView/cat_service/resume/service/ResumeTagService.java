package project.DevView.cat_service.resume.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.DevView.cat_service.ai.service.ResumeAIService;
import project.DevView.cat_service.resume.dto.ResumeTagResponse;
import project.DevView.cat_service.resume.entity.Resume;
import project.DevView.cat_service.resume.entity.ResumeTag;
import project.DevView.cat_service.resume.repository.ResumeRepository;
import project.DevView.cat_service.resume.repository.ResumeTagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeTagService {

    private final ResumeRepository resumeRepository;
    private final ResumeTagRepository resumeTagRepository;
    private final ResumeAIService resumeAIService;

    public ResumeTagResponse generateTags(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        // AI 서비스를 통해 태그 생성
        ResumeTagResponse response = resumeAIService.generateTags(resume.getContent());

        // priorityScore가 높은 순으로 정렬하여 상위 5개 태그만 선택
        List<ResumeTag> tags = response.getKeywords().stream()
            .sorted((a, b) -> b.getPriorityScore() - a.getPriorityScore())  // priorityScore 내림차순 정렬
            .limit(5)
            .map(keyword -> ResumeTag.builder()
                .resume(resume)
                .tagType(keyword.getTagType())
                .keyword(keyword.getKeyword())
                .detail(keyword.getDetail())
                .depthScore(keyword.getDepthScore())
                .priorityScore(keyword.getPriorityScore())
                .build())
            .collect(Collectors.toList());

        // DB 저장은 별도의 트랜잭션 메서드로 분리
        saveTags(tags);

        return response;
    }

    @Transactional
    protected void saveTags(List<ResumeTag> tags) {
        resumeTagRepository.saveAll(tags);
    }
} 