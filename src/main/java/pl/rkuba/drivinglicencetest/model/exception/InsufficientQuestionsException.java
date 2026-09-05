package pl.rkuba.drivinglicencetest.model.exception;

public class InsufficientQuestionsException extends RuntimeException {
    public InsufficientQuestionsException(String message) {
        super(message);
    }
}
