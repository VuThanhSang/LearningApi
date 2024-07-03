package com.example.learning_api.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class QuestionAnswersDTO {
    private String questionId;
    private List<AnswerDTO> answers;
    @Data
    public static class AnswerDTO {
        private String answerId;
        private String content;
    }
}
