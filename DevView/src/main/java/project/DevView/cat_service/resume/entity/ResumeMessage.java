package project.DevView.cat_service.resume.entity;

import jakarta.persistence.*;
import lombok.*;
import project.DevView.cat_service.global.entity.TimeStamp;

@Entity
@Table(name = "resume_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeMessage extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType type;

    @Column(nullable = false, length = 1000)
    private String content;

    @Builder
    public ResumeMessage(Resume resume,
                        MessageType type,
                        String content) {
        this.resume = resume;
        this.type = type;
        this.content = content;
    }

    public enum MessageType {
        QUESTION,    // 질문 (next-question 또는 follow-up)
        ANSWER      // 답변
    }
} 