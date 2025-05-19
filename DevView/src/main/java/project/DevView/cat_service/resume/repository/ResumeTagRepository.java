package project.DevView.cat_service.resume.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.DevView.cat_service.resume.entity.ResumeTag;

import java.util.List;

public interface ResumeTagRepository extends JpaRepository<ResumeTag, Long> {
    List<ResumeTag> findByResumeId(Long resumeId);
} 