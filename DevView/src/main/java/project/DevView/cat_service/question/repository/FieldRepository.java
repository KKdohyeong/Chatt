package project.DevView.cat_service.question.repository;

import project.DevView.cat_service.question.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long> {
    // Field.name 으로 찾기
    Optional<Field> findByName(String name);
}
