package com.example.cat_service.interview.entity;

import com.example.cat_service.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bookmark")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가 북마크했는지
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 어떤 메시지를 북마크했는지
    @ManyToOne
    @JoinColumn(name = "message_id")
    private InterviewMessage message;

    // Auditing
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
