package com.example.learning_api.dto.response.test;

import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.entity.sql.database.TestResultEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetTestProgressResponse {
    private TestResultEntity testResult;
    private List<GetQuestionsResponse.QuestionResponse> questions;
}
