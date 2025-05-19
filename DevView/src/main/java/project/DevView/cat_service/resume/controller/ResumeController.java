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

@Slf4j
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public SuccessResponse<SingleResult<ResumeResponse>> createResume(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ResumeRequest request) {
        log.debug("이력서 생성 요청 - 사용자: {}, 내용 길이: {}", user.getUsername(), request.getContent().length());
        
        ResumeResponse resume = resumeService.createResume(user.getId(), request);
        log.debug("이력서 생성 완료 - ID: {}", resume.getId());
        
        return SuccessResponse.ok(ResponseService.getSingleResult(resume));
    }

    @GetMapping("/{resumeId}")
    public SuccessResponse<SingleResult<ResumeResponse>> getResume(
            @PathVariable Long resumeId) {
        return SuccessResponse.ok(ResponseService.getSingleResult(resumeService.getResume(resumeId)));
    }

    @GetMapping("/user")
    public SuccessResponse<ListResult<ResumeResponse>> getUserResumes(
            @AuthenticationPrincipal CustomUserDetails user) {
        return SuccessResponse.ok(ResponseService.getListResult(resumeService.getUserResumes(user.getId())));
    }

    @PutMapping("/{resumeId}")
    public SuccessResponse<SingleResult<ResumeResponse>> updateResume(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ResumeRequest request) {
        return SuccessResponse.ok(ResponseService.getSingleResult(
            resumeService.updateResume(resumeId, user.getId(), request)));
    }

    @DeleteMapping("/{resumeId}")
    public SuccessResponse<SingleResult<Void>> deleteResume(
            @PathVariable Long resumeId,
            @AuthenticationPrincipal CustomUserDetails user) {
        resumeService.deleteResume(resumeId, user.getId());
        return SuccessResponse.ok(ResponseService.getSingleResult(null));
    }
} 