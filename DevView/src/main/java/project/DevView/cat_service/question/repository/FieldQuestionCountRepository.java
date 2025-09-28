package project.DevView.cat_service.question.repository;

import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.entity.FieldQuestionCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldQuestionCountRepository extends JpaRepository<FieldQuestionCount, Field> {

    @Query(
            value = "update field_question_count set question_count = question_count + 1 where field = :field",
            nativeQuery = true
    )
    @Modifying
    int increase(@Param("field") String field);

    @Query(
            value = "update field_question_count set question_count = question_count - 1 where field = :field",
            nativeQuery = true
    )
    @Modifying
    int decrease(@Param("field") String field);
}
