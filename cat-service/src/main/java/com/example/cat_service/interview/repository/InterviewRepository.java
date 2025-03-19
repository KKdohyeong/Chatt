package com.example.cat_service.interview.repository;

import com.example.cat_service.interview.entity.Field;
import com.example.cat_service.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    // 사용자 + 분야로 인터뷰 목록 찾기
    List<Interview> findByUserIdAndField(int userId, Field field);

    // 필요하다면 다른 쿼리 메서드 추가
}
