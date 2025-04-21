package project.DevView.cat_service.question.entity;

import jakarta.persistence.*;
import lombok.*;
import project.DevView.cat_service.global.entity.TimeStamp;

@Entity
@Table(name = "field")
@Getter @Setter
@NoArgsConstructor
public class Field extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Builder
    public Field(Long id,
                 String name) {
        this.id = id;
        this.name = name;
    }
}
