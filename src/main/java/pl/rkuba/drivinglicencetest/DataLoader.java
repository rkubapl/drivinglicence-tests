package pl.rkuba.drivinglicencetest;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import pl.rkuba.drivinglicencetest.model.*;
import pl.rkuba.drivinglicencetest.repository.QuestionRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {
    private static final int COL_NUM = 1;
    private static final int COL_QUESTION = 2;
    private static final int COL_ANSWER_A = 3;
    private static final int COL_ANSWER_B = 4;
    private static final int COL_ANSWER_C = 5;
    private static final int COL_CORRECT_ANSWER = 6;
    private static final int COL_MEDIA = 7;
    private static final int COL_TYPE = 8;
    private static final int COL_POINTS = 9;
    private static final int COL_CATEGORIES = 10;
    private static final Set<String> ALLOWED_BASIC_ANSWERS = Set.of("T", "N");
    private static final Set<String> ALLOWED_SPECIALIST_ANSWERS = Set.of("A", "B", "C");

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final QuestionRepository questionRepository;
    private final ResourceLoader resourceLoader;

    public DataLoader(QuestionRepository questionRepository, ResourceLoader resourceLoader) {
        this.questionRepository = questionRepository;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) throws Exception {
        if(args.length == 0 || !args[0].equals("--load")) return;
        logger.info("Loading questions from questions.csv file...");
        Resource resource = resourceLoader.getResource("classpath:questions.csv");
        questionRepository.saveAll(getQuestionsFromResource(resource));
    }

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

    private Question getQuestion(String[] line) throws Exception {
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

    private List<Question> getQuestionsFromResource(Resource resource) throws IOException, CsvValidationException {
        List<Question> questions = new ArrayList<>();
        int failed = 0;

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                try {
                    Question question = getQuestion(line);
                    if(question != null) {
                        questions.add(question);
                    }
                } catch(Exception ex) {
                    logger.error("Failed to parse questions.csv: {}", Arrays.toString(line).strip(), ex);
                    failed++;
                }
            }
        }

        logger.info("Loaded {} questions from CSV file ({} failed)", questions.size(), failed);
        return questions;
    }
}
