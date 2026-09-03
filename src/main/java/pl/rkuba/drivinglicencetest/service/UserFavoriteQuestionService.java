package pl.rkuba.drivinglicencetest.service;

import org.springframework.stereotype.Service;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.entity.UserFavoriteQuestion;
import pl.rkuba.drivinglicencetest.model.exception.QuestionNotFoundException;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.UserFavoriteQuestionRepository;

@Service
public class UserFavoriteQuestionService {
    private final QuestionRepository questionRepository;
    private final UserFavoriteQuestionRepository userFavoriteQuestionRepository;

    public UserFavoriteQuestionService(QuestionRepository questionRepository, UserFavoriteQuestionRepository userFavoriteQuestionRepository) {
        this.questionRepository = questionRepository;
        this.userFavoriteQuestionRepository = userFavoriteQuestionRepository;
    }

    public void addFavoriteQuestion(Long questionId, String userId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new QuestionNotFoundException(questionId));

        UserFavoriteQuestion favoriteQuestion = new UserFavoriteQuestion();
        favoriteQuestion.setQuestion(question);
        favoriteQuestion.setUserId(userId);
        userFavoriteQuestionRepository.save(favoriteQuestion);
    }

    public boolean removeFavoriteQuestion(Long questionId, String userId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new QuestionNotFoundException(questionId));
        return userFavoriteQuestionRepository.deleteUserFavoriteQuestionByQuestionAndUserId(question, userId) > 0;
    }
}
