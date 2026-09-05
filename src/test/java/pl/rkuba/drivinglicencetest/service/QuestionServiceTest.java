package pl.rkuba.drivinglicencetest.service;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import pl.rkuba.drivinglicencetest.QuestionFixtures;
import pl.rkuba.drivinglicencetest.model.dto.QuestionFilter;
import pl.rkuba.drivinglicencetest.model.entity.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.entity.UserAnswer;
import pl.rkuba.drivinglicencetest.model.entity.UserFavoriteQuestion;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.QuestionSpecification;
import pl.rkuba.drivinglicencetest.repository.UserAnswerRepository;
import pl.rkuba.drivinglicencetest.repository.UserFavoriteQuestionRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Transactional
@DataJpaTest
@Import({QuestionService.class, QuestionSpecification.class})
class QuestionServiceTest {
    private static final String USER_ID = "aa288db0-17c6-4931-a58f-5f70465cfe78";

    @ServiceConnection
    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Autowired
    private UserFavoriteQuestionRepository userFavoriteQuestionRepository;

    @Test
    void findsQuestionsByType() {
        questionRepository.saveAll(List.of(
                QuestionFixtures.basicQuestion(1),
                QuestionFixtures.specialistQuestion(2)
        ));

        QuestionFilter filter = new QuestionFilter();
        filter.setQuestionType("BASIC");
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        Question found = result.getContent().get(0);
        assertEquals("BASIC", found.getQuestionType());
    }

    @Test
    void findsQuestionsByCategory() {
        Question q1 = QuestionFixtures.basicQuestion(1);
        q1.setCategories(Set.of(Category.A, Category.B));

        Question q2 = QuestionFixtures.basicQuestion(2);
        q2.setCategories(Set.of(Category.B));

        questionRepository.saveAll(List.of(q1, q2));

        QuestionFilter filter = new QuestionFilter();
        filter.setCategory(Category.A);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getQuestionNumber());
    }

    @Test
    void findsQuestionsByMinPoints() {
        BasicQuestion q1 = QuestionFixtures.basicQuestion(1);
        q1.setPoints(1);
        BasicQuestion q2 = QuestionFixtures.basicQuestion(2);
        q2.setPoints(3);
        questionRepository.saveAll(List.of(q1, q2));

        QuestionFilter filter = new QuestionFilter();
        filter.setMinPoints(2);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        assertEquals(2, result.getContent().get(0).getQuestionNumber());
    }

    @Test
    void findsQuestionsByMaxPoints() {
        BasicQuestion q1 = QuestionFixtures.basicQuestion(1);
        q1.setPoints(1);
        BasicQuestion q2 = QuestionFixtures.basicQuestion(2);
        q2.setPoints(3);
        questionRepository.saveAll(List.of(q1, q2));

        QuestionFilter filter = new QuestionFilter();
        filter.setMaxPoints(2);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getQuestionNumber());
    }

    @Test
    void findsQuestionsByExactPoints() {
        BasicQuestion q1 = QuestionFixtures.basicQuestion(1);
        q1.setPoints(3);
        BasicQuestion q2 = QuestionFixtures.basicQuestion(2);
        q2.setPoints(1);
        questionRepository.saveAll(List.of(q1, q2));

        QuestionFilter filter = new QuestionFilter();
        filter.setEqPoints(3);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getQuestionNumber());
    }

    @Test
    void excludesAnsweredQuestions() {
        BasicQuestion q1 = QuestionFixtures.basicQuestion(1);
        BasicQuestion q2 = QuestionFixtures.basicQuestion(2);
        questionRepository.saveAll(List.of(q1, q2));

        UserAnswer answer = answerFor(q1, true);
        userAnswerRepository.save(answer);

        QuestionFilter filter = new QuestionFilter();
        filter.setExcludeAnswered(true);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        assertEquals(2, result.getContent().get(0).getQuestionNumber());
    }

    @Test
    void excludesCorrectlyAnsweredQuestions() {
        BasicQuestion q1 = QuestionFixtures.basicQuestion(1);
        BasicQuestion q2 = QuestionFixtures.basicQuestion(2);
        questionRepository.saveAll(List.of(q1, q2));

        userAnswerRepository.save(answerFor(q1, true));
        userAnswerRepository.save(answerFor(q2, false));

        QuestionFilter filter = new QuestionFilter();
        filter.setExcludeAnsweredCorrectly(true);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        assertEquals(2, result.getContent().get(0).getQuestionNumber());
    }

    @Test
    void returnsFavoriteQuestionsOnly() {
        BasicQuestion q1 = QuestionFixtures.basicQuestion(1);
        BasicQuestion q2 = QuestionFixtures.basicQuestion(2);
        questionRepository.saveAll(List.of(q1, q2));

        UserFavoriteQuestion favorite = new UserFavoriteQuestion();
        favorite.setUserId(USER_ID);
        favorite.setQuestion(q1);
        userFavoriteQuestionRepository.save(favorite);

        QuestionFilter filter = new QuestionFilter();
        filter.setFavoriteOnly(true);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getQuestionNumber());
    }

    @Test
    void combinesMultipleFilters() {
        BasicQuestion q1 = QuestionFixtures.basicQuestion(1);
        q1.setPoints(3);
        q1.setCategories(Set.of(Category.A));

        Question q2 = QuestionFixtures.specialistQuestion(2);
        q2.setPoints(3);
        q2.setCategories(Set.of(Category.A));

        BasicQuestion q3 = QuestionFixtures.basicQuestion(3);
        q3.setPoints(3);
        q3.setCategories(Set.of(Category.B));

        BasicQuestion q4 = QuestionFixtures.basicQuestion(4);
        q4.setPoints(1);
        q4.setCategories(Set.of(Category.A));

        questionRepository.saveAll(List.of(q1, q2, q3, q4));

        QuestionFilter filter = new QuestionFilter();
        filter.setQuestionType("BASIC");
        filter.setMinPoints(2);
        filter.setCategory(Category.A);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getQuestionNumber());
    }

    private UserAnswer answerFor(Question question, boolean correct) {
        UserAnswer answer = new UserAnswer();
        answer.setUserId(QuestionServiceTest.USER_ID);
        answer.setQuestion(question);
        answer.setGivenAnswer(GivenAnswer.T);
        answer.setCorrect(correct);
        return answer;
    }
}
