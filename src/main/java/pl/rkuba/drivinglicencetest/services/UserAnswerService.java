package pl.rkuba.drivinglicencetest.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.rkuba.drivinglicencetest.dto.controller.AnswerDto;
import pl.rkuba.drivinglicencetest.model.*;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.UserAnswerRepository;

@AllArgsConstructor
@Service
public class UserAnswerService {
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;

    public UserAnswer saveUserAnswer(AnswerDto answerDto, String userId) {
        Question question = questionRepository.findById(answerDto.questionId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        GivenAnswer answer = answerDto.answer();

        if (question instanceof BasicQuestion && answer != GivenAnswer.T && answer != GivenAnswer.F) {
            throw new IllegalArgumentException("Basic question requires answer T or F.");
        }
        if (question instanceof SpecialistQuestion && answer != GivenAnswer.A && answer != GivenAnswer.B && answer != GivenAnswer.C) {
            throw new IllegalArgumentException("Specialist question requires answer A, B or C.");
        }

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUserId(userId);
        userAnswer.setGivenAnswer(answer);
        userAnswer.setQuestion(question);
        userAnswer.setCorrect(question.isCorrect(answer));

        userAnswerRepository.save(userAnswer);
        return userAnswer;
    }
}
