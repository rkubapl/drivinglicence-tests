package pl.rkuba.drivinglicencetest.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GivenAnswer givenAnswer;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false)
    @CurrentTimestamp
    private LocalDateTime answeredAt;
}