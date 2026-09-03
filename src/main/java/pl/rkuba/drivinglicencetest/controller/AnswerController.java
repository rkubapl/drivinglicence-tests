package pl.rkuba.drivinglicencetest.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rkuba.drivinglicencetest.model.dto.AnswerRequest;
import pl.rkuba.drivinglicencetest.model.exception.InvalidAnswerException;
import pl.rkuba.drivinglicencetest.model.exception.QuestionNotFoundException;
import pl.rkuba.drivinglicencetest.service.UserAnswerService;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/answers")
public class AnswerController {
    private final UserAnswerService userAnswerService;

    @PostMapping
    public ResponseEntity<Void> saveAnswer(@RequestBody AnswerRequest answerRequest, @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();
        try {
            userAnswerService.saveUserAnswer(answerRequest, userId);
        } catch (QuestionNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (InvalidAnswerException ex) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
