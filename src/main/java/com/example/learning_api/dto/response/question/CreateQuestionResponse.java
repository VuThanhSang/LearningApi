package com.example.learning_api.dto.response.question;

import com.example.learning_api.dto.response.answer.CreateAnswerResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionResponse {
    private String id;
    private String content;
    private String description;
    private String testId;
    private String source;
    private String type;
    private String createdAt;
    private String updatedAt;
    private List<CreateAnswerResponse> answers;
}
