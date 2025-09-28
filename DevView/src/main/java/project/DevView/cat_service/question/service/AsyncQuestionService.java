package project.DevView.cat_service.question.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import project.DevView.cat_service.ai.service.ResumeAIService;
import project.DevView.cat_service.resume.dto.TagQuestionResponse;
import project.DevView.cat_service.resume.entity.ResumeTag;
import project.DevView.cat_service.resume.repository.ResumeTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncQuestionService {
    private final ResumeTagRepository resumeTagRepository;
    private final ResumeAIService resumeAIService;
    private final QuestionService questionService;

    /** ★ 반드시 별도 빈 + @Async */
    @Async("questionExecutor")
    public void generateAndSaveForTagAsync(Long tagId) {
        try {
            // (읽기 전용) 태그 로드
            ResumeTag tag = resumeTagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("tag not found: " + tagId));

            // LLM 호출 (내부에 429/5xx 재시도 로직 적용 권장)
            TagQuestionResponse resp = resumeAIService.generateQuestions(List.of(tag));

            // 결과 저장 (짧은 TX)
            questionService.saveQuestions(tag, resp);

            // 로그
            int cnt = resp.getQuestions() == null ? 0 : resp.getQuestions().size();
            log.info("[ASYNC] 태그 '{}'({}) 질문 {}개 저장 완료", tag.getKeyword(), tagId, cnt);

        } catch (Exception e) {
            log.error("[ASYNC] 태그 {} 처리 실패: {}", tagId, e.getMessage(), e);
            // 여기서 실패를 DB에 기록하고 스케줄러 재시도 대상으로 만들고 싶다면
            // question_task 테이블/상태머신을 별도로 두는 패턴을 적용하면 좋다(확장 옵션).
        }
    }
}