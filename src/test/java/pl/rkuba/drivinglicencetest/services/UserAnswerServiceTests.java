package pl.rkuba.drivinglicencetest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import pl.rkuba.drivinglicencetest.model.dto.AnswerRequest;
import pl.rkuba.drivinglicencetest.model.entity.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.entity.SpecialistQuestion;
import pl.rkuba.drivinglicencetest.model.entity.UserAnswer;
import pl.rkuba.drivinglicencetest.model.enums.AnswerLetter;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.UserAnswerRepository;
import pl.rkuba.drivinglicencetest.service.UserAnswerService;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAnswerServiceTests {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserAnswerRepository userAnswerRepository;

    @InjectMocks
    private UserAnswerService userAnswerService;

    private BasicQuestion basicQuestion() {
        BasicQuestion basicQuestion = new BasicQuestion();
        basicQuestion.setQuestion("Question");
        basicQuestion.setCorrectAnswer(true);
        basicQuestion.setQuestionNumber(1);
        basicQuestion.setMedia("image.jpg");
        basicQuestion.setPoints(3);
        basicQuestion.setCategories(Set.of(Category.B));
        return basicQuestion;
    }

    private SpecialistQuestion specialistQuestion() {
        SpecialistQuestion specialistQuestion = new SpecialistQuestion();
        specialistQuestion.setQuestion("Question");
        specialistQuestion.setQuestionNumber(1);
        specialistQuestion.setMedia("image.jpg");
        specialistQuestion.setPoints(3);
        specialistQuestion.setCategories(Set.of(Category.B));
        specialistQuestion.setAnswerA("120 km/h");
        specialistQuestion.setAnswerB("140 km/h");
        specialistQuestion.setAnswerC("No limit");
        specialistQuestion.setCorrectAnswerLetter(AnswerLetter.B);
        return specialistQuestion;
    }

    @Test
    void testBasicQuestionUserAnswerSave() {
        AnswerRequest answerRequest = new AnswerRequest(1L, GivenAnswer.T);

        BasicQuestion question = basicQuestion();
        given(questionRepository.findById(answerRequest.questionId())).willReturn(Optional.of(question));
        when(userAnswerRepository.save(any(UserAnswer.class))).thenReturn(new UserAnswer());

        Jwt mockJwt = Mockito.mock(Jwt.class);
        Mockito.when(mockJwt.getSubject()).thenReturn("ea15b99f-e02a-40eb-bbfb-c352b4b8451a");

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        userAnswerService.saveUserAnswer(answerRequest, mockJwt);

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

        Jwt mockJwt = Mockito.mock(Jwt.class);
        Mockito.when(mockJwt.getSubject()).thenReturn("ea15b99f-e02a-40eb-bbfb-c352b4b8451a");

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        userAnswerService.saveUserAnswer(answerRequest, mockJwt);

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

        Jwt mockJwt = Mockito.mock(Jwt.class);
        Mockito.when(mockJwt.getSubject()).thenReturn("ea15b99f-e02a-40eb-bbfb-c352b4b8451a");

        assertThrows(ResponseStatusException.class, () -> userAnswerService.saveUserAnswer(answerRequest, mockJwt));
    }
}
