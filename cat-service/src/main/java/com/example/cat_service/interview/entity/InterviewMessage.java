package com.example.cat_service.interview.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "interview_message")
public class InterviewMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 인터뷰(Interview)에서 발생한 메시지인지
    @ManyToOne
    @JoinColumn(name = "interview_id")
    private Interview interview;

    // 메시지 전송자 (예: "USER", "AI", "SYSTEM")
    private String sender;

    // 메시지 유형 (예: "QUESTION", "ANSWER", "FOLLOWUP_QUESTION", etc.)
    private String messageType;

    // 메시지 텍스트 (질문/답변)
    @Column(length = 3600)
    private String content;

    // 만약 원본 Question(문제은행)과 직접 연결된 메시지라면 참조
    // (꼬리 질문이면 null)
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = true)
    private Question question;

    // Auditing
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
