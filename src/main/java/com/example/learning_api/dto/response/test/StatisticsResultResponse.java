package com.example.learning_api.dto.response.test;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsResultResponse {

    private Integer totalPassed;
    private Integer totalFailed;
    private Integer totalAttempted;
    private Integer totalNotAttempted;
    private List<Question> questionSortByIncorrectRate;

    @Data
    public static class Question {
        private String id;
        private String content;
        private String description;
        private String source;
        private String type;
        private String createdAt;
        private String updatedAt;
        private Integer totalCorrect;
        private Integer totalIncorrect;
        private List<Answers> answers;
    }
    @Data
    public static class Answers {
        private String id;
        private String content;
        private boolean isCorrect;
        private String questionId;
        private String source;
        private Integer totalSelected;
        private String createdAt;
        private String updatedAt;
    }

}
