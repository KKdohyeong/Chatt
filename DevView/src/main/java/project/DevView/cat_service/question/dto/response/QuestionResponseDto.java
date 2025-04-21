package project.DevView.cat_service.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.entity.Question;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record QuestionResponseDto(
        @NotNull
        @Schema(description = "질문 id", example = "1")
        Long questionId,

        @NotNull
        @Schema(description = "질문 내용", example = "인터뷰 질문 예시입니다")
        String content,

        @NotNull
        @Schema(description = "연결된 Field 이름 목록", example = "[\"OS\",\"DB\"]")
        List<String> fields
) {
    public static QuestionResponseDto of(Question question) {
        return QuestionResponseDto.builder()
                .questionId(question.getId())
                .content(question.getContent())
                .fields(question.getFields()
                        .stream()
                        .map(Field::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}
