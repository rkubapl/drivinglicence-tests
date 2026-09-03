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
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.enums.Category;
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
    public PageResponse<Question> questions(QuestionFilter filter, @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();
        Page<Question> pageable = questionService.findQuestionByFilter(filter, userId);
        return PageResponse.of(pageable);
    }

    @GetMapping(path = "/exam")
    public List<Question> exam(@RequestParam Category category) {
        return questionService.generateExamQuestions(category);
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
