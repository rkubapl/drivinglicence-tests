package pl.rkuba.drivinglicencetest.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pl.rkuba.drivinglicencetest.model.dto.QuestionSearchSpec;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.repository.QuestionSpecification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class QuestionService {
    private final QuestionSpecification questionSpecification;
    private final EntityManager em;

    public List<Question> findQuestionByFilter(QuestionSearchSpec questionSpec, Jwt jwt) {
        String userId = jwt.getSubject();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Question> query = cb.createQuery(Question.class);
        Root<Question> root = query.from(Question.class);

        Specification<Question> spec = Specification.unrestricted();
        if(questionSpec.getQuestionType() != null) {
            spec = spec.and(questionSpecification.hasQuestionType(questionSpec.getQuestionType()));
        }
        if(questionSpec.getMinPoints() != null) {
            spec = spec.and(questionSpecification.hasMinPoints(questionSpec.getMinPoints()));
        }
        if(questionSpec.getEqPoints() != null) {
            spec = spec.and(questionSpecification.hasPoints(questionSpec.getEqPoints()));
        }
        if(questionSpec.getMaxPoints() != null) {
            spec = spec.and(questionSpecification.hasMaxPoints(questionSpec.getMaxPoints()));
        }
        if(questionSpec.getCategory() != null) {
            spec = spec.and(questionSpecification.hasCategory(questionSpec.getCategory()));
        }
        if(questionSpec.getExcludeAnswered() != null && questionSpec.getExcludeAnswered()) {
            spec = spec.and(questionSpecification.excludeAnswered(userId));
        }
        if(questionSpec.getExcludeAnsweredCorrectly() != null && questionSpec.getExcludeAnsweredCorrectly()) {
            spec = spec.and(questionSpecification.excludeAnsweredCorrectly(userId));
        }

        Predicate predicate = spec.toPredicate(root, query, cb);
        if (predicate != null) {
            query.where(predicate);
        }

        query.orderBy(cb.asc(cb.function("RANDOM", Double.class)));

        return em.createQuery(query)
                .setMaxResults(questionSpec.getQuestionsAmount())
                .getResultList();
    }

    public List<Question> generateExamQuestions(Category category, Jwt jwt) {
        //https://www.gov.pl/web/infrastruktura/prawo-jazdy
        //W części podstawowej jest 10 pytań za 3 punkty, 6 pytań za 2 punkty i 4 pytania za 1 punkt.
        //W części specjalistycznej (na poszczególne kategorie): 6 pytań za 3 punkty, 4 pytania za 2 punkty, 2 pytania za 1 punkt.

        List<QuestionSearchSpec> questionSearchSpecs = List.of(
            new QuestionSearchSpec(category,"BASIC", 3, 10),
            new QuestionSearchSpec(category,"BASIC", 2, 6),
            new QuestionSearchSpec(category,"BASIC", 1, 4),
            new QuestionSearchSpec(category,"SPECIALIST", 3, 6),
            new QuestionSearchSpec(category,"SPECIALIST", 2, 4),
            new QuestionSearchSpec(category,"SPECIALIST", 1, 2)
        );

        return questionSearchSpecs.stream()
                .map(spec -> findQuestionByFilter(spec, jwt))
                .collect(ArrayList::new, List::addAll, List::addAll);
    }
}
