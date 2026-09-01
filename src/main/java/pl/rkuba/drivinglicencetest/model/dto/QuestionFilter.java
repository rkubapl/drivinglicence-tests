package pl.rkuba.drivinglicencetest.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.rkuba.drivinglicencetest.model.enums.Category;

@NoArgsConstructor
@Getter
@Setter
public class QuestionFilter {
    private String questionType;
    private Integer minPoints;
    private Integer maxPoints;
    private Category category;
    private Boolean excludeAnswered;
    private Boolean excludeAnsweredCorrectly;

    public QuestionSearchSpec toQuestionSpec() {
        return new QuestionSearchSpec(
                this.questionType,
                this.minPoints,
                this.maxPoints,
                this.category,
                this.excludeAnswered,
                this.excludeAnsweredCorrectly,
                1,
                true);
    }
}
