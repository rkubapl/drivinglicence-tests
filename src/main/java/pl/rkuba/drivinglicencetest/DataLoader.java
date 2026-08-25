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
import pl.rkuba.drivinglicencetest.services.QuestionLoaderService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final QuestionLoaderService questionLoaderService;
    private final QuestionRepository questionRepository;
    private final ResourceLoader resourceLoader;

    public DataLoader(QuestionLoaderService questionLoaderService, QuestionRepository questionRepository, ResourceLoader resourceLoader) {
        this.questionLoaderService = questionLoaderService;
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

    private List<Question> getQuestionsFromResource(Resource resource) throws IOException, CsvValidationException {
        List<Question> questions = new ArrayList<>();
        int failed = 0;

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                try {
                    Question question = questionLoaderService.getQuestion(line);
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
