package pl.rkuba.drivinglicencetest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rkuba.drivinglicencetest.model.entity.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.entity.UserFavoriteQuestion;
import pl.rkuba.drivinglicencetest.model.exception.QuestionNotFoundException;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.UserFavoriteQuestionRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.rkuba.drivinglicencetest.QuestionFixtures.basicQuestion;

@ExtendWith(MockitoExtension.class)
public class UserFavoriteQuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserFavoriteQuestionRepository userFavoriteQuestionRepository;

    @InjectMocks
    private UserFavoriteQuestionService userFavoriteQuestionService;

    @Test
    void addFavoriteQuestionNotFound() {
        given(questionRepository.findById(1L)).willReturn(Optional.empty());
        assertThrows(QuestionNotFoundException.class, () -> userFavoriteQuestionService.addFavoriteQuestion(1L, "a2316d62-807c-47bf-8d03-e47d46fd1dfe"));
    }

    @Test
    void saveFavoriteQuestion() {
        BasicQuestion question = basicQuestion();
        given(questionRepository.findById(5L)).willReturn(Optional.of(question));

        ArgumentCaptor<UserFavoriteQuestion> captor = ArgumentCaptor.forClass(UserFavoriteQuestion.class);
        userFavoriteQuestionService.addFavoriteQuestion(5L, "ea15b99f-e02a-40eb-bbfb-c352b4b8451a");

        verify(userFavoriteQuestionRepository).save(captor.capture());
        UserFavoriteQuestion saved = captor.getValue();
        assertEquals("ea15b99f-e02a-40eb-bbfb-c352b4b8451a", saved.getUserId());
        assertEquals(question, saved.getQuestion());
    }

    @Test
    void removeFavoriteQuestionNotFound() {
        given(questionRepository.findById(1L)).willReturn(Optional.empty());
        assertThrows(QuestionNotFoundException.class, () -> userFavoriteQuestionService.removeFavoriteQuestion(1L, "a2316d62-807c-47bf-8d03-e47d46fd1dfe"));
    }

    @Test
    void removedFavoriteQuestion() {
        BasicQuestion question = basicQuestion();
        given(questionRepository.findById(5L)).willReturn(Optional.of(question));
        given(userFavoriteQuestionRepository.deleteUserFavoriteQuestionByQuestionAndUserId(question, "a2316d62-807c-47bf-8d03-e47d46fd1dfe")).willReturn(1L);

        assertTrue(userFavoriteQuestionService.removeFavoriteQuestion(5L, "a2316d62-807c-47bf-8d03-e47d46fd1dfe"));
    }

    @Test
    void notRemovedFavoriteQuestion() {
        BasicQuestion question = basicQuestion();
        given(questionRepository.findById(5L)).willReturn(Optional.of(question));
        given(userFavoriteQuestionRepository.deleteUserFavoriteQuestionByQuestionAndUserId(question, "a2316d62-807c-47bf-8d03-e47d46fd1dfe")).willReturn(0L);

        assertFalse(userFavoriteQuestionService.removeFavoriteQuestion(5L, "a2316d62-807c-47bf-8d03-e47d46fd1dfe"));
    }
}
