package pl.rkuba.drivinglicencetest.model.entity;

import org.junit.jupiter.api.Test;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

import static org.junit.jupiter.api.Assertions.*;

class BasicQuestionTest {

    private final BasicQuestion question = new BasicQuestion();

    @Test
    void acceptsOnlyTrueOrFalse() {
        assertTrue(question.isValidAnswer(GivenAnswer.T));
        assertTrue(question.isValidAnswer(GivenAnswer.F));
        assertFalse(question.isValidAnswer(GivenAnswer.A));
        assertFalse(question.isValidAnswer(GivenAnswer.B));
        assertFalse(question.isValidAnswer(GivenAnswer.C));
    }

    @Test
    void trueIsCorrectWhenCorrectAnswerIsTrue() {
        question.setCorrectAnswer(true);

        assertTrue(question.isCorrect(GivenAnswer.T));
        assertFalse(question.isCorrect(GivenAnswer.F));
    }

    @Test
    void falseIsCorrectWhenCorrectAnswerIsFalse() {
        question.setCorrectAnswer(false);

        assertTrue(question.isCorrect(GivenAnswer.F));
        assertFalse(question.isCorrect(GivenAnswer.T));
    }
}
