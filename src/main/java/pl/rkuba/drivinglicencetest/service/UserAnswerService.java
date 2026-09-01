package pl.rkuba.drivinglicencetest.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.rkuba.drivinglicencetest.model.dto.AnswerRequest;
import pl.rkuba.drivinglicencetest.model.dto.StatisticsResponse;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.entity.UserAnswer;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.UserAnswerRepository;

@AllArgsConstructor
@Service
public class UserAnswerService {
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;

    public UserAnswer saveUserAnswer(AnswerRequest answerRequest, Jwt principal) {
        String userId = principal.getSubject();
        Question question = questionRepository.findById(answerRequest.questionId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        GivenAnswer answer = answerRequest.answer();

        if (!question.isValidAnswer(answer)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not allowed answer for question");
        }

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUserId(userId);
        userAnswer.setGivenAnswer(answer);
        userAnswer.setQuestion(question);
        userAnswer.setCorrect(question.isCorrect(answer));

        userAnswerRepository.save(userAnswer);
        return userAnswer;
    }

    public StatisticsResponse getUserStatistics(Jwt principal) {
        String userId = principal.getSubject();
        StatisticsResponse response = new StatisticsResponse();
        response.setHistory(userAnswerRepository.countAnswersByDate(userId));
        response.setUniqueQuestionsDone(userAnswerRepository.countDistinctByQuestion(userId));
        response.setTotalAnswers(userAnswerRepository.countByUserId(userId));
        return response;
    }
}
