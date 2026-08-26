package pl.rkuba.drivinglicencetest.dto.controller;

import pl.rkuba.drivinglicencetest.model.GivenAnswer;

public record AnswerDto(Long questionId, GivenAnswer answer) {}
