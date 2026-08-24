package pl.rkuba.drivinglicencetest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="question_type", discriminatorType = DiscriminatorType.STRING)
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="question_type", insertable = false, updatable = false)
    protected String questionType;

    @Column(name = "question_number", unique = true)
    private Integer questionNumber;
    private String media;
    private Integer points;

    @Column(nullable = false, length = 1000)
    private String question;

    @ElementCollection(targetClass = Category.class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "question_categories",
        joinColumns = @JoinColumn(name = "question_id"),
        indexes = {
                @Index(name = "idx_question_category", columnList = "category")
        }
    )
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Set<Category> categories;

    public String toString() {
        return this.questionNumber.toString() + " " + this.question;
    }
}