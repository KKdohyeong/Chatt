package project.DevView.cat_service.question.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.DevView.cat_service.global.dto.response.SuccessResponse;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.question.dto.response.QuestionResponseDto;
import project.DevView.cat_service.question.dto.request.QuestionCreateRequest;
import project.DevView.cat_service.question.service.QuestionService;


@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Question")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 질문 생성
     */
    @PostMapping
    @Operation(summary = "질문 생성")
    public SuccessResponse<SingleResult<QuestionResponseDto>> createQuestion(
            @Valid @RequestBody QuestionCreateRequest requestDto
    ) {
        SingleResult<QuestionResponseDto> result = questionService.createQuestion(requestDto);
        return SuccessResponse.ok(result);
    }
}
