package pl.rkuba.drivinglicencetest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.rkuba.drivinglicencetest.model.dto.AnswerRequest;
import pl.rkuba.drivinglicencetest.model.dto.QuestionFilter;
import pl.rkuba.drivinglicencetest.model.dto.StatisticsResponse;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.entity.UserAnswer;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.service.QuestionService;
import pl.rkuba.drivinglicencetest.service.UserAnswerService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class QuestionController {
    private final QuestionService questionService;
    private final UserAnswerService userAnswerService;

    @GetMapping(path = "/questions")
    public List<Question> questions(QuestionFilter filter, @AuthenticationPrincipal Jwt principal) {
        return questionService.findQuestionByFilter(filter.toQuestionSpec(), principal);
    }

    @GetMapping(path = "/exam")
    public List<Question> exam(Category category, @AuthenticationPrincipal Jwt principal) {
        return questionService.generateExamQuestions(category, principal);
    }

    @PostMapping(path = "/answer")
    public UserAnswer saveAnswer(@RequestBody AnswerRequest answerRequest, @AuthenticationPrincipal Jwt principal) {
        return userAnswerService.saveUserAnswer(answerRequest, principal);
    }

    @GetMapping(path = "/statistics")
    public StatisticsResponse statistics(@AuthenticationPrincipal Jwt principal) {
        return userAnswerService.getUserStatistics(principal);
    }
}
