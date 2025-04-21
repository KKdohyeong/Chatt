package project.DevView.cat_service.question.repository;

import project.DevView.cat_service.question.entity.UserQuestionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserQuestionHistoryRepository extends JpaRepository<UserQuestionHistory, Long> {

    /**
     * userId가 이미 완료(completed=true)한 Question들의 ID 목록
     */
    @Query("""
        SELECT h.question.id
        FROM UserQuestionHistory h
        WHERE h.user.id = :userId
          AND h.completed = true
    """)
    List<Long> findAnsweredQuestionIds(@Param("userId") int userId);

    /**
     * 특정 userId & questionId에 대해 completed=true로 업데이트 (꼬리질문 종료 시점 등)
     *
     * @Modifying + @Query => UPDATE 쿼리를 실행할 수 있음.
     */
    @Modifying
    @Query("""
        UPDATE UserQuestionHistory h
        SET h.completed = true, h.answeredAt = CURRENT_TIMESTAMP
        WHERE h.user.id = :userId
          AND h.question.id = :questionId
    """)
    void markQuestionCompleted(@Param("userId") int userId,
                              @Param("questionId") Long questionId);
}
