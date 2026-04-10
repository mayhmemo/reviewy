package com.reviewy.model;

import lombok.Data;
import java.util.Map;

@Data
public class Question {
    private String id;
    private String type;
    private String correct;
    private String statement;
    private Map<String, String> options;
}
