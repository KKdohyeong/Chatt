package com.example.cat_service.interview.entity;

import com.example.cat_service.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "interview")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 사용자가 이 인터뷰를 진행했는지
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 이 인터뷰가 한 분야에 해당한다고 가정 (OS, DB 등)
    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    // Auditing
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
