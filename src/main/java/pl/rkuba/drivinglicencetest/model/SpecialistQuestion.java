package pl.rkuba.drivinglicencetest.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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

    private String correctAnswerString;
}
