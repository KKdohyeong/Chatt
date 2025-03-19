package com.example.cat_service.interview.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Question 생성 시 클라이언트에서 보낼 Request DTO
 * - content: 질문 내용
 * - fieldIds: 이미 DB에 있는 Field들의 id 목록
 */
@Getter
@Setter
public class QuestionCreateRequestDto {
    private String content;      // 질문 내용
    private List<Long> fieldIds; // 연결할 Field들의 PK 리스트
}
