package com.reviewy.service;

import com.reviewy.model.Quiz;
import com.reviewy.model.Question;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class QuizSession {
    private final Quiz quiz;
    private int currentIndex = 0;
    private int hits = 0;
    private int misses = 0;
    private final Map<String, String> userAnswers = new HashMap<>();
    private final Map<String, Boolean> validationResults = new HashMap<>();

    public QuizSession(Quiz quiz) {
        this.quiz = quiz;
    }

    public Question getCurrentQuestion() {
        return quiz.getQuestions().get(currentIndex);
    }

    public void next() {
        if (currentIndex < quiz.getQuestions().size() - 1) {
            currentIndex++;
        }
    }

    public void previous() {
        if (currentIndex > 0) {
            currentIndex--;
        }
    }

    public boolean validateAnswer(String questionId, String answer) {
        Question question = findQuestionById(questionId);
        if (question == null) return false;

        boolean isCorrect = question.getCorrect().equalsIgnoreCase(answer);
        
        // Record if not already answered
        if (!userAnswers.containsKey(questionId)) {
            userAnswers.put(questionId, answer);
            validationResults.put(questionId, isCorrect);
            if (isCorrect) hits++; else misses++;
        }
        
        return isCorrect;
    }

    public boolean isAnswered(String questionId) {
        return userAnswers.containsKey(questionId);
    }

    public Boolean getResultFor(String questionId) {
        return validationResults.get(questionId);
    }

    public String getUserAnswerFor(String questionId) {
        return userAnswers.get(questionId);
    }

    public boolean hasMoreQuestions() {
        return currentIndex < quiz.getQuestions().size() - 1;
    }

    public boolean isLastQuestion() {
        return currentIndex == quiz.getQuestions().size() - 1;
    }

    public int getRemaining() {
        return quiz.getQuestions().size() - currentIndex - 1;
    }

    public void reset() {
        this.currentIndex = 0;
        this.hits = 0;
        this.misses = 0;
        this.userAnswers.clear();
        this.validationResults.clear();
    }

    private Question findQuestionById(String id) {
        return quiz.getQuestions().stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
