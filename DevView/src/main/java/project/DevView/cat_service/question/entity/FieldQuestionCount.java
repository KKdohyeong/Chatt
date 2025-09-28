package project.DevView.cat_service.question.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "field_question_count")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FieldQuestionCount {
    
    @Id
    @Enumerated(EnumType.STRING)
    private Field field;
    
    private Long questionCount;

    public static FieldQuestionCount init(Field field, Long questionCount) {
        FieldQuestionCount fieldQuestionCount = new FieldQuestionCount();
        fieldQuestionCount.field = field;
        fieldQuestionCount.questionCount = questionCount;
        return fieldQuestionCount;
    }
}
