package pl.rkuba.drivinglicencetest.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rkuba.drivinglicencetest.model.dto.AnswerRequest;
import pl.rkuba.drivinglicencetest.model.dto.StatisticsResponse;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.entity.UserAnswer;
import pl.rkuba.drivinglicencetest.model.exception.InvalidAnswerException;
import pl.rkuba.drivinglicencetest.model.exception.QuestionNotFoundException;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.UserAnswerRepository;

@AllArgsConstructor
@Service
public class UserAnswerService {
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;

    public void saveUserAnswer(AnswerRequest answerRequest, String userId) {
        Question question = questionRepository.findById(answerRequest.questionId()).orElseThrow(() -> new QuestionNotFoundException(answerRequest.questionId()));
        GivenAnswer answer = answerRequest.answer();

        if (!question.isValidAnswer(answer)) {
            throw new InvalidAnswerException(answer);
        }

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUserId(userId);
        userAnswer.setGivenAnswer(answer);
        userAnswer.setQuestion(question);
        userAnswer.setCorrect(question.isCorrect(answer));

        userAnswerRepository.save(userAnswer);
    }

    public StatisticsResponse getUserStatistics(String userId) {
        StatisticsResponse response = new StatisticsResponse();
        response.setHistory(userAnswerRepository.countAnswersByDate(userId));
        response.setUniqueQuestionsDone(userAnswerRepository.countDistinctByQuestion(userId));
        response.setTotalAnswers(userAnswerRepository.countByUserId(userId));
        return response;
    }
}
