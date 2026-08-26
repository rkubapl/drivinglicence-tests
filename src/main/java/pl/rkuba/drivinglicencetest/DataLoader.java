package pl.rkuba.drivinglicencetest;

import lombok.AllArgsConstructor;
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

@AllArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final QuestionLoaderService questionLoaderService;
    private final QuestionRepository questionRepository;
    private final ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {
        if(args.length == 0 || !args[0].equals("--load")) return;
        logger.info("Loading questions from questions.csv file...");
        Resource resource = resourceLoader.getResource("classpath:questions.csv");
        questionRepository.saveAll(questionLoaderService.getQuestionsFromResource(resource));
    }
}
