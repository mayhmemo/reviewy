package com.reviewy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.reviewy.model.Quiz;

import java.io.File;
import java.io.IOException;

public class QuizService {
    private final ObjectMapper yamlMapper;

    public QuizService() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    public Quiz loadQuizFromYaml(String filePath) throws IOException {
        return yamlMapper.readValue(new File(filePath), Quiz.class);
    }
}
