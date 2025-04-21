package project.DevView.cat_service.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.DevView.cat_service.global.dto.response.SuccessResponse;
import project.DevView.cat_service.global.dto.response.result.ListResult;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.interview.dto.request.InterviewCreateRequestDto;
import project.DevView.cat_service.interview.dto.response.InterviewResponseDto;
import project.DevView.cat_service.interview.service.InterviewService;
import project.DevView.cat_service.user.dto.CustomUserDetails;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Tag(name = "Interview")
public class InterviewController {

    private final InterviewService interviewService;

    /**
     * 분야별 과거 인터뷰 목록 조회
     * GET /api/interviews?field=OS
     */
    @GetMapping
    @Operation(summary = "분야별 인터뷰 목록 조회")
    public SuccessResponse<ListResult<InterviewResponseDto>> getInterviewsByField(
            @RequestParam("field") String fieldName,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        var result = interviewService.findByUserAndField(user.getId(), fieldName);
        return SuccessResponse.ok(result);
    }

    /**
     * 새 인터뷰 시작
     * POST /api/interviews
     */
    @PostMapping
    @Operation(summary = "새 인터뷰 시작")
    public SuccessResponse<SingleResult<InterviewResponseDto>> createInterview(
            @Valid @RequestBody InterviewCreateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        var result = interviewService.createInterview(user.getId(), request);
        return SuccessResponse.ok(result);
    }

    /**
     * 인터뷰 상세 조회
     * GET /api/interviews/{interviewId}
     */
    @GetMapping("/{interviewId}")
    @Operation(summary = "인터뷰 상세 조회")
    public SuccessResponse<SingleResult<InterviewResponseDto>> getInterview(
            @PathVariable("interviewId") Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        var result = interviewService.getInterviewDetail(interviewId, user.getId());
        return SuccessResponse.ok(result);
    }

    /**
     * 인터뷰 종료
     * POST /api/interviews/{interviewId}/finish
     */
    @PostMapping("/{interviewId}/finish")
    @Operation(summary = "인터뷰 종료")
    public SuccessResponse<SingleResult<String>> finishInterview(
            @PathVariable("interviewId") Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        var result = interviewService.finishInterview(interviewId, user.getId());
        return SuccessResponse.ok(result);
    }
}
