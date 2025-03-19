package com.example.cat_service.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Question 응답용 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class QuestionResponseDto {
    private Long id;
    private String content;
    private List<String> fieldNames; // Question에 연결된 Field들의 이름 목록
}
