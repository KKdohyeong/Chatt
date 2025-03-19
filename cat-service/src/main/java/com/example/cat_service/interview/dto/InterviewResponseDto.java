package com.example.cat_service.interview.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 인터뷰 정보를 외부로 보낼 때 사용
 */
@Data
public class InterviewResponseDto {
    private Long interviewId;
    private int userId;            // User PK가 int
    private String fieldName;      // interview.getField().getName()
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
