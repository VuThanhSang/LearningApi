package com.example.learning_api.dto.response.test;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestSubmitResponse {
    private String id;
    private String studentId;
    private double grade;
    private int totalCorrectAnswers;
    private int totalQuestions;
    private String testId;
    private String testType;
    private boolean isPassed;
    List<List<Integer>> answers;
    private Date attendedAt;
}
