package com.reviewy.service;

import com.reviewy.model.Quiz;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class QuizServiceTest {
    @Test
    public void testLoadQuiz() throws IOException {
        QuizService service = new QuizService();
        String path = "yaml_example/yaml_test_example.yaml";
        Quiz quiz = service.loadQuizFromYaml(path);

        assertNotNull(quiz);
        assertEquals("Prova de Revisão", quiz.getTitle());
        assertEquals(2, quiz.getQuestions().size());
        
        assertEquals("q1", quiz.getQuestions().get(0).getId());
        assertEquals("a", quiz.getQuestions().get(0).getCorrect());
        assertTrue(quiz.getQuestions().get(0).getStatement().contains("Java"));
        assertEquals(4, quiz.getQuestions().get(0).getOptions().size());
    }
}
