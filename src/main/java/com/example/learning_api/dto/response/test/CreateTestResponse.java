package com.example.learning_api.dto.response.test;

import lombok.Builder;
import lombok.Data;

@Data
public class CreateTestResponse {
    private String id;
    private String name;
    private String description;
    private String createdBy;
    private String source;
    private int duration;
    private String startTime;
    private String endTime;
    private String classroomId;
    private String showResultType;
    private String createdAt;
    private String updatedAt;
}
