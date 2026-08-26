package pl.rkuba.drivinglicencetest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rkuba.drivinglicencetest.dto.controller.AnswerDto;
import pl.rkuba.drivinglicencetest.model.*;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.UserAnswerRepository;

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
        AnswerDto answerDto = new AnswerDto(1L, GivenAnswer.T);

        BasicQuestion question = basicQuestion();
        given(questionRepository.findById(answerDto.questionId())).willReturn(Optional.of(question));
        when(userAnswerRepository.save(any(UserAnswer.class))).thenReturn(new UserAnswer());

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        userAnswerService.saveUserAnswer(answerDto, "ea15b99f-e02a-40eb-bbfb-c352b4b8451a");

        verify(userAnswerRepository).save(captor.capture());
        UserAnswer saved = captor.getValue();
        assertEquals("ea15b99f-e02a-40eb-bbfb-c352b4b8451a", saved.getUserId());
        assertTrue(saved.isCorrect());
        assertEquals(GivenAnswer.T, saved.getGivenAnswer());
    }

    @Test
    void testSpecialistQuestionUserAnswerSave() {
        AnswerDto answerDto = new AnswerDto(1L, GivenAnswer.C);

        SpecialistQuestion question = specialistQuestion();
        given(questionRepository.findById(answerDto.questionId())).willReturn(Optional.of(question));
        when(userAnswerRepository.save(any(UserAnswer.class))).thenReturn(new UserAnswer());

        ArgumentCaptor<UserAnswer> captor = ArgumentCaptor.forClass(UserAnswer.class);
        userAnswerService.saveUserAnswer(answerDto, "ea15b99f-e02a-40eb-bbfb-c352b4b8451a");

        verify(userAnswerRepository).save(captor.capture());
        UserAnswer saved = captor.getValue();
        assertEquals("ea15b99f-e02a-40eb-bbfb-c352b4b8451a", saved.getUserId());
        assertFalse(saved.isCorrect());
        assertEquals(GivenAnswer.C, saved.getGivenAnswer());
    }

    @Test
    void testNotAllowedAnswerToSpecialistQuestion() {
        AnswerDto answerDto = new AnswerDto(1L, GivenAnswer.T);

        SpecialistQuestion question = specialistQuestion();
        given(questionRepository.findById(answerDto.questionId())).willReturn(Optional.of(question));

        assertThrows(IllegalArgumentException.class, () -> userAnswerService.saveUserAnswer(answerDto, "ea15b99f-e02a-40eb-bbfb-c352b4b8451a"));
    }
}
