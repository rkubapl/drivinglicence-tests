package pl.rkuba.drivinglicencetest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.rkuba.drivinglicencetest.model.QuestionFilter;
import pl.rkuba.drivinglicencetest.model.Question;
import pl.rkuba.drivinglicencetest.services.QuestionService;

import java.util.List;

@AllArgsConstructor
@RestController
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping(path = "/questions")
    public List<Question> getQuestions(@RequestBody QuestionFilter filter) {
        return questionService.findQuestionByFilter(filter);
    }
}
