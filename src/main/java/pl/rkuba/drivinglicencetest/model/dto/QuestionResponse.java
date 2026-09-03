package pl.rkuba.drivinglicencetest.model.dto;

import pl.rkuba.drivinglicencetest.model.entity.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.entity.SpecialistQuestion;
import pl.rkuba.drivinglicencetest.model.enums.GivenAnswer;

public record QuestionResponse(
        Long id,
        String questionType,
        String media,
        Integer points,
        String question,
        String answerA,
        String answerB,
        String answerC,
        GivenAnswer correctAnswer
) {
    public static QuestionResponse from(Question q) {
        String type;
        String answerA = null;
        String answerB = null;
        String answerC = null;
        GivenAnswer correctAnswer;

        if (q instanceof BasicQuestion b) {
            type = "BASIC";
            correctAnswer = b.getCorrectAnswer() ? GivenAnswer.T : GivenAnswer.F;
        } else if (q instanceof SpecialistQuestion s) {
            type = "SPECIALIST";
            answerA = s.getAnswerA();
            answerB = s.getAnswerB();
            answerC = s.getAnswerC();
            correctAnswer = GivenAnswer.valueOf(s.getCorrectAnswerLetter().name());
        } else {
            throw new IllegalStateException("Unknown question subtype: " + q.getClass().getName());
        }

        return new QuestionResponse(
                q.getId(),
                type,
                q.getMedia(),
                q.getPoints(),
                q.getQuestion(),
                answerA,
                answerB,
                answerC,
                correctAnswer
        );
    }
}
