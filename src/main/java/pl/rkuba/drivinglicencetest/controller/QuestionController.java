package pl.rkuba.drivinglicencetest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.rkuba.drivinglicencetest.dto.controller.AnswerDto;
import pl.rkuba.drivinglicencetest.model.*;
import pl.rkuba.drivinglicencetest.services.QuestionService;
import pl.rkuba.drivinglicencetest.services.UserAnswerService;

import java.util.List;

@AllArgsConstructor
@RestController
public class QuestionController {
    private final QuestionService questionService;
    private final UserAnswerService userAnswerService;

    @PostMapping(path = "/questions")
    public List<Question> getQuestions(@RequestBody QuestionFilter filter) {
        return questionService.findQuestionByFilter(filter);
    }

    @PostMapping(path = "/answer")
    public UserAnswer answer(@RequestBody AnswerDto answerDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAnswerService.saveUserAnswer(answerDto, userId);
    }
}
