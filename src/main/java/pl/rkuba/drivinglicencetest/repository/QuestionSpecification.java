package pl.rkuba.drivinglicencetest.repository;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pl.rkuba.drivinglicencetest.model.entity.*;
import pl.rkuba.drivinglicencetest.model.enums.Category;

@Component
public class QuestionSpecification {
    public Specification<Question> hasMinPoints(int points) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.ge(root.get(Question_.points), points);
    }

    public Specification<Question> hasMaxPoints(Integer points) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.le(root.get(Question_.points), points);
    }

    public Specification<Question> hasPoints(int points) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Question_.points), points);
    }

    public Specification<Question> hasQuestionType(String questionType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Question_.questionType), questionType);
    }

    public Specification<Question> hasCategory(Category category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isMember(category, root.get(Question_.categories));
    }

    public Specification<Question> excludeAnswered(String userId) {
        return (root, query, criteriaBuilder) -> {
            Subquery<UserAnswer> subquery = query.subquery(UserAnswer.class);
            Root<UserAnswer> userAnswerRoot = subquery.from(UserAnswer.class);

            subquery.select(userAnswerRoot);
            subquery.where(
                criteriaBuilder.equal(userAnswerRoot.get(UserAnswer_.question), root),
                criteriaBuilder.equal(userAnswerRoot.get(UserAnswer_.userId), userId)
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
                    criteriaBuilder.equal(userAnswerRoot.get(UserAnswer_.question), root),
                    criteriaBuilder.equal(userAnswerRoot.get(UserAnswer_.userId), userId),
                    criteriaBuilder.equal(userAnswerRoot.get(UserAnswer_.correct), true)
            );

            return criteriaBuilder.not(criteriaBuilder.exists(subquery));
        };
    }

    public Specification<Question> favoriteOnly(String userId) {
        return (root, query, criteriaBuilder) -> {
            Subquery<UserFavoriteQuestion> subquery = query.subquery(UserFavoriteQuestion.class);
            Root<UserFavoriteQuestion> userFavoriteQuestionRoot = subquery.from(UserFavoriteQuestion.class);

            subquery.select(userFavoriteQuestionRoot);
            subquery.where(
                    criteriaBuilder.equal(userFavoriteQuestionRoot.get(UserFavoriteQuestion_.question), root),
                    criteriaBuilder.equal(userFavoriteQuestionRoot.get(UserFavoriteQuestion_.userId), userId)
            );

            return criteriaBuilder.exists(subquery);
        };
    }
}

