package pl.rkuba.drivinglicencetest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("SPECIALIST")
@Getter
@Setter
@NoArgsConstructor
public class SpecialistQuestion extends Question{
    private String answerA;
    private String answerB;
    private String answerC;

    @Enumerated(EnumType.STRING)
    private AnswerLetter correctAnswerLetter;

    @Override
    public boolean isCorrect(GivenAnswer answer) {
        return correctAnswerLetter.name().equals(answer.name());
    }
}
