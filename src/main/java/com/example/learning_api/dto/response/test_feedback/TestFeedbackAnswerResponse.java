package com.example.learning_api.dto.response.test_feedback;

import com.example.learning_api.entity.sql.database.TeacherEntity;
import lombok.Data;

@Data
public class TestFeedbackAnswerResponse {
    private String id;
    private String testFeedbackId;
    private String answer;
    private String createdAt;
    private String updatedAt;
}
