package pl.rkuba.drivinglicencetest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.rkuba.drivinglicencetest.model.QuestionFilter;
import pl.rkuba.drivinglicencetest.model.Question;
import pl.rkuba.drivinglicencetest.services.QuestionService;

import java.util.List;

@RestController
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping(path = "/questions")
    public List<Question> getQuestions(@RequestBody QuestionFilter filter) {
        return questionService.findQuestionByFilter(filter);
    }
}
