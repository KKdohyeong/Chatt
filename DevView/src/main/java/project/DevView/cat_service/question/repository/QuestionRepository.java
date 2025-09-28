package project.DevView.cat_service.question.repository;

import project.DevView.cat_service.question.entity.Field;
import project.DevView.cat_service.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByField(Enum field);

    /**
     * 페이지네이션을 위한 필드별 질문 조회
     */
    @Query(
            value = "select q.id, q.field, q.question, q.answer, q.created_at, q.modified_at " +
                    "from (" +
                    "   select id from question " +
                    "   where field = :field " +
                    "   order by id desc " +
                    "   limit :limit offset :offset " +
                    ") t left join question q on t.id = q.id",
            nativeQuery = true
    )
    List<Question> findAllByField(
            @Param("field") String field,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );

    /**
     * 필드별 제한된 카운트 조회
     */
    @Query(
            value = "select count(*) from (" +
                    "   select id from question where field = :field limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long countByFieldLimited(@Param("field") String field, @Param("limit") Long limit);

    /**
     * 1) usedIds가 비었을 때 (빈 리스트)
     *    => f.name = :fieldName 만으로 검색
     *    => 오름차순, LIMIT 1
     */
    @Query(value = """
        SELECT q.*
        FROM question q
        WHERE UPPER(q.field) = UPPER(:fieldName)
        ORDER BY q.id ASC
        LIMIT 1
    """, nativeQuery = true)
    Optional<Question> findOneWithoutUsedIds(@Param("fieldName") String fieldName);

    @Query(value = """
        SELECT q.*
        FROM question q
        WHERE UPPER(q.field) = UPPER(:fieldName)
           AND q.id NOT IN (:usedIds)
         ORDER BY q.id ASC
         LIMIT 1
    """, nativeQuery = true)
    Optional<Question> findOneWithUsedIds(
            @Param("fieldName") String fieldName,
            @Param("usedIds") List<Long> usedIds
    );

    /**
     * 질문 내용으로 질문 찾기
     */
    @Query("SELECT q FROM Question q WHERE q.answer = :answer")
    Optional<Question> findByContent(@Param("answer") String answer);

}
