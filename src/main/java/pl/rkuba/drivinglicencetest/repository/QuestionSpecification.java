package pl.rkuba.drivinglicencetest.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pl.rkuba.drivinglicencetest.model.Category;
import pl.rkuba.drivinglicencetest.model.Question;

@Component
public class QuestionSpecification {
    public Specification<Question> hasMinPoints(int points) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.ge(root.get("points"), points);
    }

    public Specification<Question> hasMaxPoints(int points) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.le(root.get("points"), points);
    }

    public Specification<Question> hasQuestionType(String questionType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("questionType"), questionType);
    }

    public Specification<Question> hasCategory(Category category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isMember(category, root.get("categories"));
    }
}

