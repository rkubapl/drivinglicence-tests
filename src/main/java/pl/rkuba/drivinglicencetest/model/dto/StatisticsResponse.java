package pl.rkuba.drivinglicencetest.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class StatisticsResponse {
    private Integer totalAnswers;
    private Integer uniqueQuestionsDone;
    private List<UserStats> history;
}
