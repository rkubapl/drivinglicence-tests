package pl.rkuba.drivinglicencetest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rkuba.drivinglicencetest.model.dto.AnswerRequest;
import pl.rkuba.drivinglicencetest.model.entity.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.entity.SpecialistQuestion;
import pl.rkuba.drivinglicencetest.model.entity.UserAnswer;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;
import pl.rkuba.drivinglicencetest.model.exception.InvalidAnswerException;
import pl.rkuba.drivinglicencetest.model.exception.QuestionNotFoundException;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.UserAnswerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static pl.rkuba.drivinglicencetest.QuestionFixtures.basicQuestion;
import static pl.rkuba.drivinglicencetest.QuestionFixtures.specialistQuestion;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAnswerServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserAnswerRepository userAnswerRepository;

    @InjectMocks
    private UserAnswerService userAnswerService;

    @Test
    void testBasicQuestionUserAnswerSave() {
        AnswerRequest answerRequest = new AnswerRequest(1L, GivenAnswer.T);

        BasicQuestion question = basicQuestion();
        given(questionRepository.findById(answerRequest.questionId())).willReturn(Optional.of(question));
        when(userAnswerRepository.save(any(UserAnswer.class))).thenReturn(new UserAnswer());

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        userAnswerService.saveUserAnswer(answerRequest, "ea15b99f-e02a-40eb-bbfb-c352b4b8451a");

        verify(userAnswerRepository).save(captor.capture());
        UserAnswer saved = captor.getValue();
        assertEquals("ea15b99f-e02a-40eb-bbfb-c352b4b8451a", saved.getUserId());
        assertTrue(saved.isCorrect());
        assertEquals(GivenAnswer.T, saved.getGivenAnswer());
    }

    @Test
    void testSpecialistQuestionUserAnswerSave() {
        AnswerRequest answerRequest = new AnswerRequest(1L, GivenAnswer.C);

        SpecialistQuestion question = specialistQuestion();
        given(questionRepository.findById(answerRequest.questionId())).willReturn(Optional.of(question));
        when(userAnswerRepository.save(any(UserAnswer.class))).thenReturn(new UserAnswer());

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        userAnswerService.saveUserAnswer(answerRequest, "ea15b99f-e02a-40eb-bbfb-c352b4b8451a");

        verify(userAnswerRepository).save(captor.capture());
        UserAnswer saved = captor.getValue();
        assertEquals("ea15b99f-e02a-40eb-bbfb-c352b4b8451a", saved.getUserId());
        assertFalse(saved.isCorrect());
        assertEquals(GivenAnswer.C, saved.getGivenAnswer());
    }

    @Test
    void testNotAllowedAnswerToSpecialistQuestion() {
        AnswerRequest answerRequest = new AnswerRequest(1L, GivenAnswer.T);

        SpecialistQuestion question = specialistQuestion();
        given(questionRepository.findById(answerRequest.questionId())).willReturn(Optional.of(question));

        assertThrows(InvalidAnswerException.class, () -> userAnswerService.saveUserAnswer(answerRequest, "ea15b99f-e02a-40eb-bbfb-c352b4b8451a"));
    }

    @Test
    void testInvalidQuestionId() {
        AnswerRequest answerRequest = new AnswerRequest(1000L, GivenAnswer.T);
        given(questionRepository.findById(answerRequest.questionId())).willReturn(Optional.empty());

        assertThrows(QuestionNotFoundException.class, () -> userAnswerService.saveUserAnswer(answerRequest, "ea15b99f-e02a-40eb-bbfb-c352b4b8451a"));
    }
}
