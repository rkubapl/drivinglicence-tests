package pl.rkuba.drivinglicencetest.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.rkuba.drivinglicencetest.model.dto.QuestionFilter;
import pl.rkuba.drivinglicencetest.model.dto.RandomQuestionFilter;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.enums.Category;
import pl.rkuba.drivinglicencetest.model.exception.InsufficientQuestionsException;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.QuestionSpecification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class QuestionService {
    private final QuestionSpecification questionSpecification;
    private final EntityManager em;
    private final QuestionRepository questionRepository;

    public Page<Question> findQuestionByFilter(QuestionFilter questionSpec, String userId) {
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
        if(questionSpec.isExcludeAnswered()) {
            spec = spec.and(questionSpecification.excludeAnswered(userId));
        }
        if(questionSpec.isExcludeAnsweredCorrectly()) {
            spec = spec.and(questionSpecification.excludeAnsweredCorrectly(userId));
        }
        if(questionSpec.isFavoriteOnly()) {
            spec = spec.and(questionSpecification.favoriteOnly(userId));
        }

        return questionRepository.findBy(spec,
                fluent -> fluent.page(PageRequest.of(questionSpec.getPage(), questionSpec.getPageSize())));
    }

    public List<Question> getRandomQuestions(RandomQuestionFilter randomQuestionFilter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Question> query = cb.createQuery(Question.class);
        Root<Question> root = query.from(Question.class);

        Specification<Question> spec = Specification.unrestricted();
        spec = spec.and(questionSpecification.hasQuestionType(randomQuestionFilter.questionType()));
        spec = spec.and(questionSpecification.hasCategory(randomQuestionFilter.category()));
        spec = spec.and(questionSpecification.hasPoints(randomQuestionFilter.points()));
        query.where(spec.toPredicate(root, query, cb));

        query.orderBy(cb.asc(cb.function("RANDOM", Double.class)));

        TypedQuery<Question> typedQuery = em.createQuery(query)
                .setMaxResults(randomQuestionFilter.questionsAmount());

        List<Question> resultList = typedQuery.getResultList();
        if(resultList.size() != randomQuestionFilter.questionsAmount()) {
            throw new InsufficientQuestionsException("Not enough questions for " + randomQuestionFilter.category().name());
        }
        return resultList;
    }

    public List<Question> generateExamQuestions(Category category) {
        //https://www.gov.pl/web/infrastruktura/prawo-jazdy
        //W części podstawowej jest 10 pytań za 3 punkty, 6 pytań za 2 punkty i 4 pytania za 1 punkt.
        //W części specjalistycznej (na poszczególne kategorie): 6 pytań za 3 punkty, 4 pytania za 2 punkty, 2 pytania za 1 punkt.

        List<RandomQuestionFilter> questionSearchSpecs = List.of(
            new RandomQuestionFilter(category,"BASIC", 3, 10),
            new RandomQuestionFilter(category,"BASIC", 2, 6),
            new RandomQuestionFilter(category,"BASIC", 1, 4),
            new RandomQuestionFilter(category,"SPECIALIST", 3, 6),
            new RandomQuestionFilter(category,"SPECIALIST", 2, 4),
            new RandomQuestionFilter(category,"SPECIALIST", 1, 2)
        );

        return questionSearchSpecs.stream()
                .map(this::getRandomQuestions)
                .collect(ArrayList::new, List::addAll, List::addAll);
    }
}
