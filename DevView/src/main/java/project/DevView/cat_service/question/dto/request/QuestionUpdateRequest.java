package project.DevView.cat_service.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record QuestionUpdateRequest(
        @NotBlank
        @Schema(description = "질문 내용", example = "수정된 인터뷰 질문입니다")
        String question,

        @NotBlank
        @Schema(description = "답변 내용", example = "수정된 인터뷰 답변입니다")
        String answer
) {}
