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
import pl.rkuba.drivinglicencetest.model.entity.*;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;
import pl.rkuba.drivinglicencetest.model.exception.InsufficientQuestionsException;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.QuestionSpecification;
import pl.rkuba.drivinglicencetest.repository.UserAnswerRepository;
import pl.rkuba.drivinglicencetest.repository.UserFavoriteQuestionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@Transactional
@DataJpaTest
@Import({QuestionService.class, QuestionSpecification.class})
class QuestionServiceTest {
    private static final String USER_ID = "aa288db0-17c6-4931-a58f-5f70465cfe78";
    private static final String OTHER_USER_ID = "7e0c9b2f-4a1b-4c3d-8e5f-123456789abc";

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
        q1.setCategories(Set.of(Category.A));

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
        q2.setPoints(2);
        BasicQuestion q3 = QuestionFixtures.basicQuestion(3);
        q3.setPoints(3);
        questionRepository.saveAll(List.of(q1, q2, q3));

        QuestionFilter filter = new QuestionFilter();
        filter.setMinPoints(2);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(2, result.getTotalElements());
        assertEquals(Set.of(2, 3), result.getContent().stream()
                .map(Question::getQuestionNumber)
                .collect(Collectors.toSet()));
    }

    @Test
    void findsQuestionsByMaxPoints() {
        BasicQuestion q1 = QuestionFixtures.basicQuestion(1);
        q1.setPoints(1);
        BasicQuestion q2 = QuestionFixtures.basicQuestion(2);
        q2.setPoints(2);
        BasicQuestion q3 = QuestionFixtures.basicQuestion(3);
        q3.setPoints(3);
        questionRepository.saveAll(List.of(q1, q2, q3));

        QuestionFilter filter = new QuestionFilter();
        filter.setMaxPoints(2);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(2, result.getTotalElements());
        assertEquals(Set.of(1, 2), result.getContent().stream()
                .map(Question::getQuestionNumber)
                .collect(Collectors.toSet()));
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

        userAnswerRepository.save(answerFor(USER_ID, q1, true));
        userAnswerRepository.save(answerFor(OTHER_USER_ID, q2, true));

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
        BasicQuestion q3 = QuestionFixtures.basicQuestion(3);
        questionRepository.saveAll(List.of(q1, q2, q3));

        userAnswerRepository.save(answerFor(USER_ID, q1, true));
        userAnswerRepository.save(answerFor(USER_ID, q2, false));
        userAnswerRepository.save(answerFor(OTHER_USER_ID, q3, true));

        QuestionFilter filter = new QuestionFilter();
        filter.setExcludeAnsweredCorrectly(true);
        Page<Question> result = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(2, result.getTotalElements());
        assertEquals(Set.of(2, 3), result.getContent().stream()
                .map(Question::getQuestionNumber)
                .collect(Collectors.toSet()));
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

        UserFavoriteQuestion otherFavorite = new UserFavoriteQuestion();
        otherFavorite.setUserId(OTHER_USER_ID);
        otherFavorite.setQuestion(q2);
        userFavoriteQuestionRepository.save(otherFavorite);

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

    @Test
    void returnsAllQuestionsWhenNoFiltersSet() {
        questionRepository.saveAll(List.of(
                QuestionFixtures.basicQuestion(1),
                QuestionFixtures.basicQuestion(2),
                QuestionFixtures.specialistQuestion(3)
        ));

        Page<Question> result = questionService.findQuestionByFilter(new QuestionFilter(), USER_ID);

        assertEquals(3, result.getTotalElements());
    }

    @Test
    void paginatesResults() {
        questionRepository.saveAll(List.of(
                QuestionFixtures.basicQuestion(1),
                QuestionFixtures.basicQuestion(2),
                QuestionFixtures.basicQuestion(3),
                QuestionFixtures.basicQuestion(4),
                QuestionFixtures.basicQuestion(5)
        ));

        QuestionFilter filter = new QuestionFilter();
        filter.setPageSize(2);
        filter.setPage(0);
        Page<Question> first = questionService.findQuestionByFilter(filter, USER_ID);

        assertEquals(5, first.getTotalElements());
        assertEquals(3, first.getTotalPages());
        assertEquals(2, first.getContent().size());

        filter.setPage(2);
        Page<Question> last = questionService.findQuestionByFilter(filter, USER_ID);
        assertEquals(1, last.getContent().size());
    }

    @Test
    void examThrowsWhenRequestedCategoryHasNoQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.addAll(basic(1, 3, 10));
        questions.addAll(basic(11, 2, 10));
        questions.addAll(basic(21, 1, 10));
        questions.addAll(specialist(31, 3, 10));
        questions.addAll(specialist(41, 2, 10));
        questions.addAll(specialist(51, 1, 10));
        questions.forEach(q -> q.setCategories(Set.of(Category.A)));

        questionRepository.saveAll(questions);

        assertThrows(InsufficientQuestionsException.class,
                () -> questionService.generateExamQuestions(Category.B));
    }

    @Test
    void generatesValidExam() {
        List<Question> questions = new ArrayList<>();
        questions.addAll(basic(1, 3, 10));
        questions.addAll(basic(11, 2, 10));
        questions.addAll(basic(21, 1, 10));
        questions.addAll(specialist(31, 3, 10));
        questions.addAll(specialist(41, 2, 10));
        questions.addAll(specialist(51, 1, 10));

        questionRepository.saveAll(questions);
        List<Question> exam = questionService.generateExamQuestions(Category.B);

        assertEquals(32, exam.size());
        assertEquals(10, count(exam, "BASIC", 3));
        assertEquals(6, count(exam, "BASIC", 2));
        assertEquals(4, count(exam, "BASIC", 1));
        assertEquals(6, count(exam, "SPECIALIST", 3));
        assertEquals(4, count(exam, "SPECIALIST", 2));
        assertEquals(2, count(exam, "SPECIALIST", 1));

        long distinct = exam.stream().map(Question::getQuestionNumber).distinct().count();
        assertEquals(32, distinct);
    }

    private UserAnswer answerFor(String userId, Question question, boolean correct) {
        UserAnswer answer = new UserAnswer();
        answer.setUserId(userId);
        answer.setQuestion(question);
        answer.setGivenAnswer(GivenAnswer.T);
        answer.setCorrect(correct);
        return answer;
    }

    private List<Question> basic(int startNumber, int points, int amount) {
        List<Question> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            BasicQuestion q = QuestionFixtures.basicQuestion(startNumber + i);
            q.setPoints(points);
            list.add(q);
        }
        return list;
    }

    private List<Question> specialist(int startNumber, int points, int amount) {
        List<Question> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            SpecialistQuestion q = QuestionFixtures.specialistQuestion(startNumber + i);
            q.setPoints(points);
            list.add(q);
        }
        return list;
    }

    private long count(List<Question> qs, String type, int points) {
        return qs.stream().filter(q -> type.equals(q.getQuestionType()) && q.getPoints() == points).count();
    }
}
