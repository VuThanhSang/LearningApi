package com.example.learning_api.dto.request.test;

import lombok.Data;

@Data
public class CreateExitLogRequest {
    private String studentId;
    private String testResultId;
    private String time;
}
