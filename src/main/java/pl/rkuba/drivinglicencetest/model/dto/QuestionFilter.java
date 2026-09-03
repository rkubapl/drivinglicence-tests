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
    private Integer eqPoints;
    private Integer maxPoints;
    private Category category;
    private boolean excludeAnswered = false;
    private boolean excludeAnsweredCorrectly = false;
    private boolean favoriteOnly = false;

    private int pageSize = 10;
    private int page = 0;
}
