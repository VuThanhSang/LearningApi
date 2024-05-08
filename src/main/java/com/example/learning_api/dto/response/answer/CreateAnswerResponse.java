package com.example.learning_api.dto.response.answer;


import lombok.Builder;
import lombok.Data;

@Data
public class CreateAnswerResponse {
    private String id;
    private String questionId;
    private String content;
    private boolean isCorrect;
    private String source;
    private String createdAt;
    private String updatedAt;
}
