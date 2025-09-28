package project.DevView.cat_service.question.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.DevView.cat_service.global.dto.response.SuccessResponse;
import project.DevView.cat_service.global.dto.response.result.ListResult;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.global.service.ResponseService;
import project.DevView.cat_service.question.dto.response.QuestionResponseDto;
import project.DevView.cat_service.question.dto.response.QuestionWithStatusDto;
import project.DevView.cat_service.question.dto.response.QuestionPageResponse;
import project.DevView.cat_service.question.dto.request.QuestionCreateRequest;
import project.DevView.cat_service.question.dto.request.QuestionUpdateRequest;
import project.DevView.cat_service.question.service.QuestionService;
import project.DevView.cat_service.user.dto.CustomUserDetails;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Question", description = "면접 질문 관련 API")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @Hidden
    public SuccessResponse<SingleResult<QuestionResponseDto>> createQuestion(
            @Valid @RequestBody QuestionCreateRequest requestDto
    ) {
        SingleResult<QuestionResponseDto> result = questionService.createQuestion(requestDto);
        return SuccessResponse.ok(result);
    }
    
    @GetMapping
    @Operation(
        summary = "특정 분야의 모든 질문 조회",
        description = "특정 분야(예: OS, DB, Network 등)에 속한 모든 질문을 조회합니다."
    )
    public SuccessResponse<ListResult<QuestionResponseDto>> getAllQuestionsByField(
            @Parameter(description = "분야 이름", example = "OS")
            @RequestParam("field") String fieldName
    ) {
        var result = questionService.getAllQuestionsByField(fieldName);
        return SuccessResponse.ok(result);
    }
    
    @GetMapping("/with-status")
    @Operation(
        summary = "특정 분야의 모든 질문 조회 (답변 여부 포함)",
        description = "특정 분야의 모든 질문을 조회하고, 로그인한 사용자가 각 질문에 답변했는지 여부를 포함합니다."
    )
    public SuccessResponse<ListResult<QuestionWithStatusDto>> getAllQuestionsWithStatusByField(
            @Parameter(description = "분야 이름", example = "OS")
            @RequestParam("field") String fieldName,
            @Parameter(description = "인증된 사용자 정보")
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        var result = questionService.getAllQuestionsWithStatusByField(user.getId(), fieldName);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/{questionId}")
    @Operation(
        summary = "단일 질문 조회",
        description = "특정 ID의 질문을 조회합니다."
    )
    public SuccessResponse<SingleResult<QuestionResponseDto>> readQuestion(
            @Parameter(description = "질문 ID", example = "1")
            @PathVariable Long questionId
    ) {
        QuestionResponseDto result = questionService.readQuestion(questionId);
        return SuccessResponse.ok(ResponseService.getSingleResult(result));
    }

    @PutMapping("/{questionId}")
    @Operation(
        summary = "질문 수정",
        description = "특정 ID의 질문을 수정합니다."
    )
    public SuccessResponse<SingleResult<QuestionResponseDto>> updateQuestion(
            @Parameter(description = "질문 ID", example = "1")
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionUpdateRequest request
    ) {
        QuestionResponseDto result = questionService.updateQuestion(questionId, request.question(), request.answer());
        return SuccessResponse.ok(ResponseService.getSingleResult(result));
    }

    @DeleteMapping("/{questionId}")
    @Operation(
        summary = "질문 삭제",
        description = "특정 ID의 질문을 삭제합니다."
    )
    public SuccessResponse<SingleResult<Void>> deleteQuestion(
            @Parameter(description = "질문 ID", example = "1")
            @PathVariable Long questionId
    ) {
        questionService.deleteQuestion(questionId);
        return SuccessResponse.ok(ResponseService.getSingleResult(null));
    }

    @GetMapping("/paged")
    @Operation(
        summary = "페이지네이션을 통한 필드별 질문 조회",
        description = "특정 분야의 질문을 페이지네이션으로 조회합니다."
    )
    public SuccessResponse<SingleResult<QuestionPageResponse>> readAllByField(
            @Parameter(description = "분야 이름", example = "OS")
            @RequestParam("field") String fieldName,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam("page") Long page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam("pageSize") Long pageSize
    ) {
        QuestionPageResponse result = questionService.readAllByField(fieldName, page, pageSize);
        return SuccessResponse.ok(ResponseService.getSingleResult(result));
    }

    @GetMapping("/fields/{fieldName}/count")
    @Operation(
        summary = "필드별 질문 개수 조회",
        description = "특정 분야의 질문 개수를 조회합니다."
    )
    public SuccessResponse<SingleResult<Long>> countByField(
            @Parameter(description = "분야 이름", example = "OS")
            @PathVariable String fieldName,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam("page") Long page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam("pageSize") Long pageSize
    ) {
        Long result = questionService.countByField(fieldName, page, pageSize);
        return SuccessResponse.ok(ResponseService.getSingleResult(result));
    }
}
