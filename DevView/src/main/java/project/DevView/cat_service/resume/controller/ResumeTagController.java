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

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeTagController {

    private final ResumeTagService resumeTagService;
    private final TagQuestionService tagQuestionService;

    @PostMapping("/{resumeId}/tags")
    public SuccessResponse<SingleResult<ResumeTagResponse>> generateTags(
            @PathVariable Long resumeId) {
        return SuccessResponse.ok(ResponseService.getSingleResult(
            resumeTagService.generateTags(resumeId)));
    }

    @PostMapping("/{resumeId}/questions")
    public SuccessResponse<SingleResult<TagQuestionResponse>> generateQuestions(
            @PathVariable Long resumeId) {
        return SuccessResponse.ok(ResponseService.getSingleResult(
            tagQuestionService.generateQuestionsForResume(resumeId)));
    }

    @PatchMapping("/{resumeId}/questions/{questionId}/complete")
    public SuccessResponse<SingleResult<Void>> markQuestionAsCompleted(
            @PathVariable Long resumeId,
            @PathVariable Long questionId) {
        tagQuestionService.markQuestionAsCompleted(resumeId, questionId);
        return SuccessResponse.ok(ResponseService.getSingleResult(null));
    }
} 