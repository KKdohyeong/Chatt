package project.DevView.cat_service.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record QuestionCreateRequest(
        @NotBlank
        @Schema(description = "질문 내용", example = "인터뷰 질문 예시입니다")
        String content,

        @NotEmpty
        @Schema(description = "연결할 Field들의 ID 목록", example = "[1,2,3]")
        List<Long> fieldIds
) {}
