package com.example.learning_api.dto.request.test;

import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import lombok.Data;

import java.util.List;

@Data
public class SaveProgressRequest {
    private String testResultId;
    private List<QuestionAndAnswer> questionAndAnswers;
    @Data
    public static class QuestionAndAnswer {
        private String questionId;
        private List<String> answers;
        private List<String> textAnswers;
    }
}
