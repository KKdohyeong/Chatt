package project.DevView.cat_service.question.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.DevView.cat_service.global.dto.response.SuccessResponse;
import project.DevView.cat_service.global.dto.response.result.SingleResult;
import project.DevView.cat_service.question.dto.request.FieldCreateRequestDto;
import project.DevView.cat_service.question.dto.response.FieldResponseDto;
import project.DevView.cat_service.question.service.FieldService;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@Tag(name = "Field")
public class FieldController {

    private final FieldService fieldService;

    /**
     * 새 Field 추가
     */
    @PostMapping
    @Operation(summary = "새 Field 추가")
    public SuccessResponse<SingleResult<FieldResponseDto>> createField(
            @Valid @RequestBody FieldCreateRequestDto request
    ) {
        SingleResult<FieldResponseDto> result = fieldService.createField(request);
        return SuccessResponse.ok(result);
    }
}
