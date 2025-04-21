package project.DevView.cat_service.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import project.DevView.cat_service.question.entity.Field;

@Builder
public record FieldResponseDto(
        @NotNull
        @Schema(description = "Field id", example = "1")
        Long fieldId,

        @NotNull
        @Schema(description = "Field 이름", example = "OS")
        String name
) {
    public static FieldResponseDto of(Field field) {
        return FieldResponseDto.builder()
                .fieldId(field.getId())
                .name(field.getName())
                .build();
    }
}
