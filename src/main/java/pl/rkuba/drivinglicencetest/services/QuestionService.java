package pl.rkuba.drivinglicencetest.services;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.rkuba.drivinglicencetest.model.Question;
import pl.rkuba.drivinglicencetest.model.QuestionFilter;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;
import pl.rkuba.drivinglicencetest.repository.QuestionSpecification;

import java.util.List;

@AllArgsConstructor
@Service
public class QuestionService {
    private final QuestionSpecification questionSpecification;
    private final QuestionRepository questionRepository;

    public List<Question> findQuestionByFilter(QuestionFilter questionFilter) {
        Specification<Question> spec = Specification.unrestricted();

        if(questionFilter.questionType != null) {
            spec = spec.and(questionSpecification.hasQuestionType(questionFilter.questionType));
        }
        if(questionFilter.minPoints != null) {
            spec = spec.and(questionSpecification.hasMinPoints(questionFilter.minPoints));
        }
        if(questionFilter.maxPoints != null) {
            spec = spec.and(questionSpecification.hasMaxPoints(questionFilter.maxPoints));
        }
        if(questionFilter.category != null) {
            spec = spec.and(questionSpecification.hasCategory(questionFilter.category));
        }

        return questionRepository.findAll(spec);
    }




}
