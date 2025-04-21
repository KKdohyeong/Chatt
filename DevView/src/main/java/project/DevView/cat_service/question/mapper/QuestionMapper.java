package project.DevView.cat_service.question.mapper;

import project.DevView.cat_service.question.dto.request.QuestionCreateRequest;
import project.DevView.cat_service.question.entity.Question;
import project.DevView.cat_service.question.entity.Field;

import java.util.Set;

public class QuestionMapper {

    /** Request Record → Question 엔티티 */
    public static Question from(QuestionCreateRequest request, Set<Field> fields) {
        return Question.builder()
                .content(request.content())
                .fields(fields)
                .build();
    }
}
