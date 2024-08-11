package com.example.learning_api.dto.response.test;

import lombok.Data;
import java.util.List;
import java.util.Date;

@Data
public class TestResultsForClassroomResponse {
    private String testId;
    private String testName;
    private List<StudentResult> students;

    @Data
    public static class StudentResult {
        private String studentId;
        private List<TestResult> result;
    }

    @Data
    public static class TestResult {
        private Double grade;
        private Boolean isPassed;
        private String state;
        private String attendedAt;
        private String finishedAt;
    }
}