package pl.rkuba.drivinglicencetest.controller;

import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.rkuba.drivinglicencetest.model.dto.PageResponse;
import pl.rkuba.drivinglicencetest.model.dto.QuestionFilter;
import pl.rkuba.drivinglicencetest.model.dto.QuestionResponse;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.model.exception.InsufficientQuestionsException;
import pl.rkuba.drivinglicencetest.service.QuestionService;
import pl.rkuba.drivinglicencetest.service.UserFavoriteQuestionService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final UserFavoriteQuestionService userFavoriteQuestionService;

    @GetMapping
    public PageResponse<QuestionResponse> questions(QuestionFilter filter, @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();
        Page<Question> page = questionService.findQuestionByFilter(filter, userId);
        return PageResponse.of(page.map(QuestionResponse::from));
    }

    @GetMapping(path = "/exam")
    public ResponseEntity<List<QuestionResponse>> exam(@RequestParam Category category) {
        List<QuestionResponse> questions;
        try {
            questions = questionService.generateExamQuestions(category).stream()
                    .map(QuestionResponse::from)
                    .toList();
        } catch (InsufficientQuestionsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(questions);
    }

    @PostMapping(path = "/{questionId}/favorite")
    public ResponseEntity<Void> addFavorite(@PathVariable Long questionId, @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();
        try {
            userFavoriteQuestionService.addFavoriteQuestion(questionId, userId);
        } catch(DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(path = "/{questionId}/favorite")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long questionId, @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();
        return userFavoriteQuestionService.removeFavoriteQuestion(questionId, userId) ?
                    ResponseEntity.noContent().build() : ResponseEntity.notFound().build();

    }
}
