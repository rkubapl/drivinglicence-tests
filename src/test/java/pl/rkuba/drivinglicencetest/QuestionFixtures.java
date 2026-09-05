package pl.rkuba.drivinglicencetest;

import pl.rkuba.drivinglicencetest.model.entity.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.entity.SpecialistQuestion;
import pl.rkuba.drivinglicencetest.model.enums.AnswerLetter;
import pl.rkuba.drivinglicencetest.model.enums.Category;

import java.util.Set;

public final class QuestionFixtures {
    private QuestionFixtures() {}

    public static BasicQuestion basicQuestion() {
        BasicQuestion q = new BasicQuestion();
        q.setQuestion("Question");
        q.setCorrectAnswer(true);
        q.setQuestionNumber(1);
        q.setMedia("image.jpg");
        q.setPoints(3);
        q.setCategories(Set.of(Category.B));
        q.setQuestionType("BASIC");
        return q;
    }

    public static BasicQuestion basicQuestion(int questionNumber) {
        BasicQuestion basicQuestion = basicQuestion();
        basicQuestion.setId(null);
        basicQuestion.setQuestionNumber(questionNumber);
        return basicQuestion;
    }

    public static SpecialistQuestion specialistQuestion() {
        SpecialistQuestion q = new SpecialistQuestion();
        q.setQuestion("Question");
        q.setQuestionNumber(1);
        q.setMedia("image.jpg");
        q.setPoints(3);
        q.setCategories(Set.of(Category.B));
        q.setAnswerA("120 km/h");
        q.setAnswerB("140 km/h");
        q.setAnswerC("No limit");
        q.setCorrectAnswerLetter(AnswerLetter.B);
        q.setQuestionType("SPECIALIST");
        return q;
    }

    public static SpecialistQuestion specialistQuestion(int questionNumber) {
        SpecialistQuestion specialistQuestion = specialistQuestion();
        specialistQuestion.setId(null);
        specialistQuestion.setQuestionNumber(questionNumber);
        return specialistQuestion;
    }


}
