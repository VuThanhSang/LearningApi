package com.example.learning_api.dto.request.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestSubmitRequest {
    @NotBlank
    private String testResultId;
    @NotBlank
    private List<QuestionAndAnswer> questionAndAnswers;
    @Data
    public static class QuestionAndAnswer {
        private String questionId;
        private List<String> answers;
        private List<String> textAnswers;
    }

}
