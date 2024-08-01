package com.example.learning_api.dto.response.test;

import com.example.learning_api.dto.common.QuestionAnswersDTO;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import lombok.Data;

import java.util.List;

@Data
public class TestResultResponse {
    private String testId;
    private String testType;
    private double grade;
    private boolean isPassed;
    private String attendedAt;
    private String createdAt;
    private String finishedAt;
    private List<GetQuestionsResponse.QuestionResponse> questions;

}
