package project.DevView.cat_service.question.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import project.DevView.cat_service.question.entity.Field;

@Builder
public record FieldResponseDto(
        @NotNull
        Long fieldId,

        @NotNull
        String name
) {
    public static FieldResponseDto of(Field field) {
        return FieldResponseDto.builder()
                .fieldId(field.getId())
                .name(field.getName())
                .build();
    }
}
