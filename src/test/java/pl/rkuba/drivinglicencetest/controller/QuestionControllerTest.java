package pl.rkuba.drivinglicencetest.controller;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.rkuba.drivinglicencetest.config.SecurityConfig;
import pl.rkuba.drivinglicencetest.model.entity.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.model.exception.QuestionNotFoundException;
import pl.rkuba.drivinglicencetest.service.QuestionService;
import pl.rkuba.drivinglicencetest.service.UserFavoriteQuestionService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.rkuba.drivinglicencetest.QuestionFixtures.basicQuestion;

@WebMvcTest(QuestionController.class)
@Import(SecurityConfig.class)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private UserFavoriteQuestionService userFavoriteQuestionService;

    @Test
    void returnsQuestionsPage() throws Exception {
        BasicQuestion question = basicQuestion();
        question.setId(1L);
        Page<Question> page = new PageImpl<>(List.of(question));
        given(questionService.findQuestionByFilter(any(), anyString())).willReturn(page);

        mockMvc.perform(get("/v1/questions").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].questionType").value("BASIC"))
                .andExpect(jsonPath("$.content[0].correctAnswer").value("T"));
    }

    @Test
    void addsFavorite() throws Exception {
        mockMvc.perform(post("/v1/questions/1/favorite").with(jwt()))
                .andExpect(status().isCreated());
    }

    @Test
    void returnsNotFoundWhenFavoriteQuestionMissing() throws Exception {
        doThrow(new QuestionNotFoundException(1L))
                .when(userFavoriteQuestionService).addFavoriteQuestion(any(), anyString());

        mockMvc.perform(post("/v1/questions/1/favorite").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void removesFavorite() throws Exception {
        given(userFavoriteQuestionService.removeFavoriteQuestion(any(), anyString())).willReturn(true);

        mockMvc.perform(delete("/v1/questions/1/favorite").with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void failToRemoveFavorite() throws Exception {
        given(userFavoriteQuestionService.removeFavoriteQuestion(any(), anyString())).willReturn(false);

        mockMvc.perform(delete("/v1/questions/1/favorite").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsExamQuestions() throws Exception {
        BasicQuestion question = basicQuestion();
        question.setId(1L);
        given(questionService.generateExamQuestions(Category.B)).willReturn(List.of(question));

        mockMvc.perform(get("/v1/questions/exam")
                        .param("category", "B")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionType").value("BASIC"))
                .andExpect(jsonPath("$[0].correctAnswer").value("T"));
    }

    @Test
    void returnsConflictWhenFavoriteAlreadyExists() throws Exception {
        ConstraintViolationException cause = new ConstraintViolationException("duplicate", null, "sql");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate", cause);
        doThrow(ex).when(userFavoriteQuestionService).addFavoriteQuestion(any(), anyString());

        mockMvc.perform(post("/v1/questions/1/favorite").with(jwt()))
                .andExpect(status().isConflict());
    }
}
