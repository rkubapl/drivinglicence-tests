package pl.rkuba.drivinglicencetest.model.dto;

import pl.rkuba.drivinglicencetest.model.enums.Category;

public record RandomQuestionFilter (
        Category category,
        String questionType,
        int points,
        int questionsAmount
) {}
