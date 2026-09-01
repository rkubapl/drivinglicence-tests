package pl.rkuba.drivinglicencetest.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

import java.util.Set;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="question_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Question {
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

    @ElementCollection(targetClass = Category.class, fetch = FetchType.LAZY)
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

    public abstract boolean isCorrect(GivenAnswer answer);
    public abstract boolean isValidAnswer(GivenAnswer answer);
}