package pl.rkuba.drivinglicencetest.repository;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.entity.UserAnswer;

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

    public Specification<Question> hasPoints(int points) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("points"), points);
    }

    public Specification<Question> hasQuestionType(String questionType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("questionType"), questionType);
    }

    public Specification<Question> hasCategory(Category category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isMember(category, root.get("categories"));
    }

    public Specification<Question> excludeAnswered(String userId) {
        return (root, query, criteriaBuilder) -> {
            Subquery<UserAnswer> subquery = query.subquery(UserAnswer.class);
            Root<UserAnswer> userAnswerRoot = subquery.from(UserAnswer.class);

            subquery.select(userAnswerRoot);
            subquery.where(
                criteriaBuilder.equal(userAnswerRoot.get("question"), root),
                criteriaBuilder.equal(userAnswerRoot.get("userId"), userId)
            );

            return criteriaBuilder.not(criteriaBuilder.exists(subquery));
        };
    }

    public Specification<Question> excludeAnsweredCorrectly(String userId) {
        return (root, query, criteriaBuilder) -> {
            Subquery<UserAnswer> subquery = query.subquery(UserAnswer.class);
            Root<UserAnswer> userAnswerRoot = subquery.from(UserAnswer.class);

            subquery.select(userAnswerRoot);
            subquery.where(
                    criteriaBuilder.equal(userAnswerRoot.get("question"), root),
                    criteriaBuilder.equal(userAnswerRoot.get("userId"), userId),
                    criteriaBuilder.equal(userAnswerRoot.get("correct"), true)
            );

            return criteriaBuilder.not(criteriaBuilder.exists(subquery));
        };
    }
}

