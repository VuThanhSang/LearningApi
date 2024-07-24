package com.example.learning_api.dto.response.test;

import lombok.Data;

@Data
public class StartTestResponse {
    private String testResultId;
    private String testId;
    private String studentId;
    private String attendedAt;
}
