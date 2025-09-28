package project.DevView.cat_service.question.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class QuestionPageResponse {
    private final List<QuestionResponseDto> items;
    private final Long totalCount;

    public static QuestionPageResponse of(List<QuestionResponseDto> items, Long totalCount) {
        return new QuestionPageResponse(items, totalCount);
    }
}
