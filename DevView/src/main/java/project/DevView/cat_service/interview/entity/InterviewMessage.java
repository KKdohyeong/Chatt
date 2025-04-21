package project.DevView.cat_service.interview.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import project.DevView.cat_service.global.entity.TimeStamp;
import project.DevView.cat_service.question.entity.Question;

@Entity
@Table(name = "interview_message")
@Getter @Setter
@NoArgsConstructor
public class InterviewMessage extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @Column(nullable = false, length = 20)
    private String sender;

    @Column(nullable = false, length = 40)
    private String messageType;

    @Column(nullable = false, length = 3600)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Builder
    public InterviewMessage(Long id,
                            Interview interview,
                            String sender,
                            String messageType,
                            String content,
                            Question question) {
        this.id = id;
        this.interview = interview;
        this.sender = sender;
        this.messageType = messageType;
        this.content = content;
        this.question = question;
    }
}
