package pl.rkuba.drivinglicencetest.model.entity;

import org.junit.jupiter.api.Test;
import pl.rkuba.drivinglicencetest.model.enums.AnswerLetter;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

import static org.junit.jupiter.api.Assertions.*;

class SpecialistQuestionTest {

    private final SpecialistQuestion question = new SpecialistQuestion();

    @Test
    void acceptsOnlyABC() {
        assertTrue(question.isValidAnswer(GivenAnswer.A));
        assertTrue(question.isValidAnswer(GivenAnswer.B));
        assertTrue(question.isValidAnswer(GivenAnswer.C));
        assertFalse(question.isValidAnswer(GivenAnswer.T));
        assertFalse(question.isValidAnswer(GivenAnswer.F));
    }

    @Test
    void correctLetterMatches() {
        question.setCorrectAnswerLetter(AnswerLetter.B);

        assertTrue(question.isCorrect(GivenAnswer.B));
        assertFalse(question.isCorrect(GivenAnswer.A));
        assertFalse(question.isCorrect(GivenAnswer.C));
        assertFalse(question.isCorrect(GivenAnswer.T));
        assertFalse(question.isCorrect(GivenAnswer.F));
    }
}
