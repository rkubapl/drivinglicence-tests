package pl.rkuba.drivinglicencetest.model.exception;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(Long questionId) {
        super("Question not found: " + questionId);
    }
}
