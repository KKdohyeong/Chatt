package com.example.cat_service.interview.dto;

import lombok.Data;

/**
 * 새 인터뷰 시작 시, 분야 이름을 입력받기 위한 DTO
 * 예) { "field": "OS" }
 */
@Data
public class InterviewCreateRequestDto {
    private String field; // Field.name
}
