package com.example.learning_api.dto.response.test;

import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import lombok.Data;

import java.util.List;

@Data
public class GetTestDetailResponse {
    private int totalQuestions;
    private String name;
    private String description;
    private String source;
    private String id;
    private int duration;
    private List<GetQuestionsResponse.QuestionResponse> questions;
}
