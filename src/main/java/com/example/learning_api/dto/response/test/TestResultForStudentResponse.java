package com.example.learning_api.dto.response.test;

import lombok.Data;

import java.util.List;

@Data
public class TestResultForStudentResponse {
//    private String id;
    private String testName;
    private List<TestResult> results;

    @Data
    public static class TestResult {
        private Double grade;
        private Boolean isPassed;
        private String state;
        private String attendedAt;
        private String finishedAt;

    }
}