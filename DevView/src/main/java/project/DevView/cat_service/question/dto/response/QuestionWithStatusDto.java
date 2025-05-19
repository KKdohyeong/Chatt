package project.DevView.cat_service.question.dto.response;

import java.util.List;

public record QuestionWithStatusDto(
    Long id,
    String content,
    List<String> fields,
    boolean answered
) {
} 