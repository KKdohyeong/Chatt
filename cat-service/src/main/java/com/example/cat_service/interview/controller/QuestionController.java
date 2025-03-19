package com.example.cat_service.interview.controller;

import com.example.cat_service.interview.dto.QuestionCreateRequestDto;
import com.example.cat_service.interview.dto.QuestionResponseDto;
import com.example.cat_service.interview.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 1) 질문(내용) + 복수 Field 추가
     */
    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestion(@RequestBody QuestionCreateRequestDto requestDto) {
        QuestionResponseDto responseDto = questionService.createQuestion(
                requestDto.getContent(),
                requestDto.getFieldIds()
        );
        return ResponseEntity.ok(responseDto);
    }
}
