package pl.rkuba.drivinglicencetest.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("BASIC")
@Getter
@Setter
@NoArgsConstructor
public class BasicQuestion extends Question{
    private Boolean correctAnswer;

    @Override
    public boolean isCorrect(GivenAnswer answer) {
        return correctAnswer ? answer == GivenAnswer.T : answer == GivenAnswer.F;
    }
}
