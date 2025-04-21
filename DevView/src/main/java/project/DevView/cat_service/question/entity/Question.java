package project.DevView.cat_service.question.entity;

import jakarta.persistence.*;
import lombok.*;
import project.DevView.cat_service.global.entity.TimeStamp;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "question")
@Getter @Setter
@NoArgsConstructor
public class Question extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "question_field",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "field_id")
    )
    private Set<Field> fields = new HashSet<>();

    @Column(length = 3600, nullable = false)
    private String content;

    @Builder
    public Question(Long id,
                    Set<Field> fields,
                    String content) {
        this.id = id;
        this.fields = fields;
        this.content = content;
    }
}
