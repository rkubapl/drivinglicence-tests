package pl.rkuba.drivinglicencetest.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.rkuba.drivinglicencetest.model.enums.Category;

@Getter
@Setter
public class QuestionSearchSpec {
    private String questionType;
    private Integer minPoints;
    private Integer eqPoints;
    private Integer maxPoints;
    private Category category;
    private Boolean excludeAnswered;
    private Boolean excludeAnsweredCorrectly;

    private Integer questionsAmount;
    private Boolean random;

    public QuestionSearchSpec(Category category, String questionType, Integer eqPoints, Integer questionsAmount) {
        this.eqPoints = eqPoints;
        this.questionType = questionType;
        this.category = category;
        this.questionsAmount = questionsAmount;
    }

    public QuestionSearchSpec(String questionType, Integer minPoints, Integer maxPoints, Category category, Boolean excludeAnswered, Boolean excludeAnsweredCorrectly, Integer questionsAmount, Boolean random) {
        this.questionType = questionType;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.category = category;
        this.excludeAnswered = excludeAnswered;
        this.excludeAnsweredCorrectly = excludeAnsweredCorrectly;
        this.questionsAmount = questionsAmount;
        this.random = random;
    }
}
