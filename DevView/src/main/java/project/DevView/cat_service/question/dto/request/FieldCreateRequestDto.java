package project.DevView.cat_service.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;


@Builder
public record FieldCreateRequestDto(
        @NotBlank
        @Schema(description = "Field 이름", example = "OS")
        String name
) {
}
