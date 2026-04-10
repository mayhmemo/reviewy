package com.reviewy.model;

import lombok.Data;
import java.util.List;

@Data
public class Quiz {
    private String title;
    private List<Question> questions;
}
