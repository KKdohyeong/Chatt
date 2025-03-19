package com.example.cat_service.interview.entity;

import com.example.cat_service.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_question_history")
public class UserQuestionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 사용자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 어떤 질문
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    // 사용자가 이 질문을 최종적으로 답변 완료한 시각 (필요 없으면 제거 가능)
    private LocalDateTime answeredAt;

    // 다 끝냈는지, 중간 스킵했는지 등 상태 구분
    private Boolean completed;

    // Auditing
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;


}
