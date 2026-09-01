package pl.rkuba.drivinglicencetest.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.rkuba.drivinglicencetest.model.enums.AnswerLetter;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

@Entity
@DiscriminatorValue("SPECIALIST")
@Getter
@Setter
@NoArgsConstructor
public class SpecialistQuestion extends Question {
    private String answerA;
    private String answerB;
    private String answerC;

    @Enumerated(EnumType.STRING)
    private AnswerLetter correctAnswerLetter;

    @Override
    public boolean isCorrect(GivenAnswer answer) {
        return correctAnswerLetter.name().equals(answer.name());
    }

    @Override
    public boolean isValidAnswer(GivenAnswer answer) {
        return answer == GivenAnswer.A || answer == GivenAnswer.B || answer == GivenAnswer.C;
    }
}
