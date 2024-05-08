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
    private String createdAt;
    private String updatedAt;
}
