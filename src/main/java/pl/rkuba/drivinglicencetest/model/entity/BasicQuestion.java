package pl.rkuba.drivinglicencetest.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

@Entity
@DiscriminatorValue("BASIC")
@Getter
@Setter
@NoArgsConstructor
public class BasicQuestion extends Question {
    private Boolean correctAnswer;

    @Override
    public boolean isCorrect(GivenAnswer answer) {
        return correctAnswer ? answer == GivenAnswer.T : answer == GivenAnswer.F;
    }

    @Override
    public boolean isValidAnswer(GivenAnswer answer) {
        return answer == GivenAnswer.T || answer == GivenAnswer.F;
    }
}
