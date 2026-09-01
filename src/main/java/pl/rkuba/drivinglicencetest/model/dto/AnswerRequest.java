package pl.rkuba.drivinglicencetest.model.dto;

import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

public record AnswerRequest(Long questionId, GivenAnswer answer) {}
