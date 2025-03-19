package com.example.cat_service.interview.controller;

import com.example.cat_service.interview.dto.InterviewCreateRequestDto;
import com.example.cat_service.interview.dto.InterviewResponseDto;
import com.example.cat_service.interview.entity.Field;
import com.example.cat_service.interview.service.FieldService;
import com.example.cat_service.interview.service.InterviewService;
import com.example.cat_service.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final FieldService fieldService;

    /**
     * (A) 분야 별 과거 인터뷰 목록 조회 (유지)
     *     GET /api/interviews?field=OS
     */
    @GetMapping
    public ResponseEntity<List<InterviewResponseDto>> getInterviewsByField(
            @RequestParam String field,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        int userId = user.getId();
        Field fieldEntity = fieldService.getFieldByName(field);
        List<InterviewResponseDto> interviewList = interviewService.findByUserAndField(userId, fieldEntity);
        return ResponseEntity.ok(interviewList);
    }

    /**
     * (B) 새 인터뷰 시작
     *     POST /api/interviews  body: { "field": "OS" }
     *     => JSON 응답: InterviewResponseDto {interviewId, fieldName, ...}
     */
    @PostMapping
    public ResponseEntity<InterviewResponseDto> createInterview(
            @RequestBody InterviewCreateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        int userId = user.getId();
        Field fieldEntity = fieldService.getFieldByName(request.getField());
        InterviewResponseDto result = interviewService.createInterview(userId, fieldEntity);

        return ResponseEntity.ok(result);
    }

    /**
     * (C) 인터뷰 상세 조회
     *     GET /api/interviews/{interviewId}
     */
    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponseDto> getInterview(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        int userId = user.getId();
        InterviewResponseDto dto = interviewService.getInterviewDetail(interviewId, userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * (D) 인터뷰 종료
     *     POST /api/interviews/{interviewId}/finish
     */
    @PostMapping("/{interviewId}/finish")
    public ResponseEntity<String> finishInterview(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        int userId = user.getId();
        interviewService.finishInterview(interviewId, userId);
        return ResponseEntity.ok("Interview finished");
    }

}
