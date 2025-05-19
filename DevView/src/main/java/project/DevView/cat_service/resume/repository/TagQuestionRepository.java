package project.DevView.cat_service.resume.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.DevView.cat_service.resume.entity.TagQuestion;

import java.util.List;

public interface TagQuestionRepository extends JpaRepository<TagQuestion, Long> {
    List<TagQuestion> findByResumeTagIdAndIsCompletedFalse(Long resumeTagId);
} 