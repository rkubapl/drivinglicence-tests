package pl.rkuba.drivinglicencetest.model.exception;

import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

public class InvalidAnswerException extends RuntimeException {
    public InvalidAnswerException(GivenAnswer givenAnswer) {
        super("Invalid answer: " + givenAnswer.name());
    }
}
