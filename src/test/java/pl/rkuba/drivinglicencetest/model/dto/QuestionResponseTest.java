package pl.rkuba.drivinglicencetest.model.dto;

import org.junit.jupiter.api.Test;
import pl.rkuba.drivinglicencetest.QuestionFixtures;
import pl.rkuba.drivinglicencetest.model.entity.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.entity.SpecialistQuestion;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

import static org.junit.jupiter.api.Assertions.*;

class QuestionResponseTest {
    @Test
    void mapsBasicQuestion() {
        BasicQuestion question = QuestionFixtures.basicQuestion();
        question.setId(1L);
        question.setCorrectAnswer(true);

        QuestionResponse response = QuestionResponse.from(question);

        assertEquals(1L, response.id());
        assertEquals("BASIC", response.questionType());
        assertEquals(question.getQuestion(), response.question());
        assertEquals(GivenAnswer.T, response.correctAnswer());
        assertNull(response.answerA());
        assertNull(response.answerB());
        assertNull(response.answerC());
    }

    @Test
    void mapsBasicQuestionWithFalseAnswer() {
        BasicQuestion question = QuestionFixtures.basicQuestion();
        question.setCorrectAnswer(false);

        QuestionResponse response = QuestionResponse.from(question);

        assertEquals(GivenAnswer.F, response.correctAnswer());
    }

    @Test
    void mapsSpecialistQuestion() {
        SpecialistQuestion question = QuestionFixtures.specialistQuestion();

        QuestionResponse response = QuestionResponse.from(question);

        assertEquals("SPECIALIST", response.questionType());
        assertEquals("120 km/h", response.answerA());
        assertEquals("140 km/h", response.answerB());
        assertEquals("No limit", response.answerC());
        assertEquals(GivenAnswer.B, response.correctAnswer());
    }
}
