package project.DevView.cat_service.resume.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.DevView.cat_service.global.dto.response.SuccessResponse;
import project.DevView.cat_service.global.dto.response.result.ListResult;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.global.service.ResponseService;
import project.DevView.cat_service.resume.dto.ResumeRequest;
import project.DevView.cat_service.resume.dto.ResumeResponse;
import project.DevView.cat_service.resume.service.ResumeService;
import project.DevView.cat_service.user.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume", description = "이력서 관련 API")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    @Operation(
        summary = "이력서 생성",
        description = "새로운 이력서를 생성합니다."
    )
    public SuccessResponse<SingleResult<ResumeResponse>> createResume(
            @Parameter(description = "인증된 사용자 정보")
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "이력서 생성 요청", required = true)
            @RequestBody ResumeRequest request) {
        log.debug("이력서 생성 요청 - 사용자: {}, 내용 길이: {}", user.getUsername(), request.getContent().length());
        
        ResumeResponse resume = resumeService.createResume(user.getId(), request);
        log.debug("이력서 생성 완료 - ID: {}", resume.getId());
        
        return SuccessResponse.ok(ResponseService.getSingleResult(resume));
    }

    @GetMapping("/{resumeId}")
    @Hidden
    public SuccessResponse<SingleResult<ResumeResponse>> getResume(
            @PathVariable Long resumeId) {
        return SuccessResponse.ok(ResponseService.getSingleResult(resumeService.getResume(resumeId)));
    }

    @GetMapping("/user")
    @Hidden
    public SuccessResponse<ListResult<ResumeResponse>> getUserResumes(
            @AuthenticationPrincipal CustomUserDetails user) {
        return SuccessResponse.ok(ResponseService.getListResult(resumeService.getUserResumes(user.getId())));
    }

    @PutMapping("/{resumeId}")
    @Hidden
    public SuccessResponse<SingleResult<ResumeResponse>> updateResume(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ResumeRequest request) {
        return SuccessResponse.ok(ResponseService.getSingleResult(
            resumeService.updateResume(resumeId, user.getId(), request)));
    }

    @DeleteMapping("/{resumeId}")
    @Hidden
    public SuccessResponse<SingleResult<Void>> deleteResume(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal CustomUserDetails user) {
        resumeService.deleteResume(resumeId, user.getId());
        return SuccessResponse.ok(ResponseService.getSingleResult(null));
    }

    @PostMapping("/{resumeId}/follow-up")
    @Operation(
        summary = "이력서 기반 꼬리 질문 생성",
        description = "이력서 내용을 기반으로 답변에 대한 꼬리 질문을 생성합니다."
    )
    public SuccessResponse<SingleResult<Map<String, String>>> createFollowUpQuestion(
            @Parameter(description = "이력서 ID", example = "1")
            @PathVariable Long resumeId,
            @Parameter(description = "답변 내용", example = "{\"content\": \"저는 Java와 Spring을 주로 사용하여 백엔드 개발을 하고 있습니다.\"}")
            @RequestBody Map<String, String> body,
            @Parameter(description = "인증된 사용자 정보")
            @AuthenticationPrincipal CustomUserDetails user) {
        
        String answer = body.get("content");
        String followUp = resumeService.createFollowUpQuestion(resumeId, answer);
        return SuccessResponse.ok(ResponseService.getSingleResult(Map.of("followUp", followUp)));
    }
} 