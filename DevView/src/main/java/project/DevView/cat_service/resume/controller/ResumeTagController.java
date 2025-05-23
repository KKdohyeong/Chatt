package project.DevView.cat_service.resume.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.DevView.cat_service.resume.dto.ResumeTagResponse;
import project.DevView.cat_service.resume.dto.TagQuestionResponse;
import project.DevView.cat_service.resume.service.ResumeTagService;
import project.DevView.cat_service.resume.service.TagQuestionService;
import project.DevView.cat_service.global.dto.response.SuccessResponse;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.global.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Tag", description = "이력서 태그 및 질문 관련 API")
public class ResumeTagController {

    private final ResumeTagService resumeTagService;
    private final TagQuestionService tagQuestionService;

    @PostMapping("/{resumeId}/tags")
    @Operation(
        summary = "이력서 태그 및 질문 생성",
        description = "이력서 내용을 분석하여 태그와 질문을 생성합니다. 생성된 태그는 기술 스택, 아키텍처, 프로젝트 경험 등을 포함합니다."
    )
    public SuccessResponse<SingleResult<ResumeTagResponse>> generateTagsAndQuestions(
            @Parameter(description = "이력서 ID", example = "1")
            @PathVariable Long resumeId) {
        return SuccessResponse.ok(ResponseService.getSingleResult(
            resumeTagService.generateTagsAndQuestions(resumeId)));
    }

    @GetMapping("/{resumeId}/next-question")
    @Operation(
        summary = "다음 질문 가져오기",
        description = "이력서 태그를 기반으로 다음 질문을 가져옵니다. 우선순위가 높은 태그부터 순차적으로 질문이 생성됩니다."
    )
    public SuccessResponse<SingleResult<TagQuestionResponse.QuestionItem>> getNextQuestion(
            @Parameter(description = "이력서 ID", example = "1")
            @PathVariable Long resumeId) {
        return SuccessResponse.ok(ResponseService.getSingleResult(
            tagQuestionService.getNextQuestion(resumeId)));
    }

    @PatchMapping("/{resumeId}/questions/{questionId}/complete")
    @Hidden
    public SuccessResponse<SingleResult<Void>> markQuestionAsCompleted(
            @PathVariable Long resumeId,
            @PathVariable Long questionId) {
        tagQuestionService.markQuestionAsCompleted(resumeId, questionId);
        return SuccessResponse.ok(ResponseService.getSingleResult(null));
    }

    @GetMapping("/{resumeId}/tags")
    @Hidden
    public SuccessResponse<SingleResult<ResumeTagResponse>> getTags(
            @PathVariable Long resumeId) {
        return SuccessResponse.ok(ResponseService.getSingleResult(
            resumeTagService.getTags(resumeId)));
    }
} 