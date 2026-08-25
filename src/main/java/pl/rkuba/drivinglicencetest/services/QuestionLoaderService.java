package pl.rkuba.drivinglicencetest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.rkuba.drivinglicencetest.model.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.Category;
import pl.rkuba.drivinglicencetest.model.Question;
import pl.rkuba.drivinglicencetest.model.SpecialistQuestion;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class QuestionLoaderService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionLoaderService.class);

    public static final int COL_NUM = 1;
    public static final int COL_QUESTION = 2;
    public static final int COL_ANSWER_A = 3;
    public static final int COL_ANSWER_B = 4;
    public static final int COL_ANSWER_C = 5;
    public static final int COL_CORRECT_ANSWER = 6;
    public static final int COL_MEDIA = 7;
    public static final int COL_TYPE = 8;
    public static final int COL_POINTS = 9;
    public static final int COL_CATEGORIES = 10;
    public static final Set<String> ALLOWED_BASIC_ANSWERS = Set.of("T", "N");
    public static final Set<String> ALLOWED_SPECIALIST_ANSWERS = Set.of("A", "B", "C");

    private BasicQuestion createBasicQuestion(String[] line) {
        BasicQuestion basicQuestion = new BasicQuestion();

        String correctAnswer = line[COL_CORRECT_ANSWER].toUpperCase();
        if(!ALLOWED_BASIC_ANSWERS.contains(correctAnswer)) {
            throw new IllegalArgumentException(String.format("Correct answer \"%s\" is not valid for basic type question", line[COL_CORRECT_ANSWER]));
        }
        basicQuestion.setCorrectAnswer(correctAnswer.equals("T"));

        return basicQuestion;
    }

    private SpecialistQuestion createSpecialistQuestion(String[] line) {
        if(Stream.of(line[COL_ANSWER_A], line[COL_ANSWER_B], line[COL_ANSWER_C]).anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("All answers of specialist question must be not empty.");
        }

        SpecialistQuestion specialistQuestion = new SpecialistQuestion();
        specialistQuestion.setAnswerA(line[COL_ANSWER_A]);
        specialistQuestion.setAnswerB(line[COL_ANSWER_B]);
        specialistQuestion.setAnswerC(line[COL_ANSWER_C]);

        String correctAnswer = line[COL_CORRECT_ANSWER].toUpperCase();
        if(!ALLOWED_SPECIALIST_ANSWERS.contains(correctAnswer)) {
            throw new IllegalArgumentException(String.format("Correct answer \"%s\" is not valid for specialist type question", line[COL_CORRECT_ANSWER]));
        }
        specialistQuestion.setCorrectAnswerString(correctAnswer);
        return specialistQuestion;
    }

    public Question getQuestion(String[] line) {
        if (line == null || line[COL_NUM].isEmpty()) return null;

        Question question;
        if(line[COL_ANSWER_A].isEmpty()) {
            if(line[COL_TYPE].startsWith("S")) {
                logger.info("Question ID = {} is type SPECIALIST (\"SPECJALISTYCZNY\"), but answers fields are empty. Changed type to BASIC (\"PODSTAWOWY\").", line[COL_NUM]);
            }
            question = createBasicQuestion(line);
        } else {
            if(line[COL_TYPE].startsWith("P")) {
                logger.info("Question ID = {} is type BASIC (\"PODSTAWOWY\"), but answers fields are not empty. Changed type to SPECIALIST (\"SPECJALISTYCZNY\").", line[COL_NUM]);
            }
            question = createSpecialistQuestion(line);
        }

        question.setQuestionNumber(Integer.valueOf(line[COL_NUM]));
        question.setQuestion(line[COL_QUESTION].trim());

        question.setMedia(line[COL_MEDIA]);
        question.setPoints(Integer.valueOf(line[COL_POINTS]));

        Set<Category> categories = Arrays.stream(line[COL_CATEGORIES].split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Category::valueOf)
                .collect(Collectors.toSet());

        question.setCategories(categories);
        return question;
    }

}
