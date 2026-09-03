package pl.rkuba.drivinglicencetest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.rkuba.drivinglicencetest.model.dto.StatisticsResponse;
import pl.rkuba.drivinglicencetest.service.UserAnswerService;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserAnswerService userAnswerService;

    @GetMapping(path = "/statistics")
    public StatisticsResponse statistics(@AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();
        return userAnswerService.getUserStatistics(userId);
    }
}
