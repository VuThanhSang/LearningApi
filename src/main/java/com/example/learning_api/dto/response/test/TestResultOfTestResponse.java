package com.example.learning_api.dto.response.test;

import lombok.Data;

@Data
public class TestResultOfTestResponse {
    private TestInfo testInfo;
    private String studentId;
    private int grade;
    private String resultId;
    private boolean isPassed;
    private String attendedAt;
    private String finishedAt;
    private String state;
    private String testId;

    @Data
    public static class TestInfo {
        private String name;
        private String description;
        private int duration;
        private String classroomId;
        private String teacherId;
        private String id;
    }
}
