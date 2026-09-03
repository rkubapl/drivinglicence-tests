package pl.rkuba.drivinglicencetest.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.rkuba.drivinglicencetest.model.exception.QuestionNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<String> handleRuntime() {
        return ResponseEntity.notFound().build();
    }
}
