package pl.rkuba.drivinglicencetest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.rkuba.drivinglicencetest.config.SecurityConfig;
import pl.rkuba.drivinglicencetest.model.dto.AnswerRequest;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;
import pl.rkuba.drivinglicencetest.model.exception.InvalidAnswerException;
import pl.rkuba.drivinglicencetest.model.exception.QuestionNotFoundException;
import pl.rkuba.drivinglicencetest.service.UserAnswerService;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnswerController.class)
@Import(SecurityConfig.class)
class AnswerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAnswerService userAnswerService;

    @Test
    void saveAnswer() throws Exception {
        AnswerRequest answerRequest = new AnswerRequest(1L, GivenAnswer.T);

        mockMvc.perform(post("/v1/answers")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void saveInvalidAnswer() throws Exception {
        AnswerRequest answerRequest = new AnswerRequest(1L, GivenAnswer.T);
        doThrow(new InvalidAnswerException(GivenAnswer.T)).when(userAnswerService).saveUserAnswer(any(), anyString());

        mockMvc.perform(post("/v1/answers")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveNotFoundQuestion() throws Exception {
        AnswerRequest answerRequest = new AnswerRequest(1L, GivenAnswer.T);
        doThrow(new QuestionNotFoundException(1L)).when(userAnswerService).saveUserAnswer(any(), anyString());

        mockMvc.perform(post("/v1/answers")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isNotFound());
    }
}
