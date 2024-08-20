package com.example.learning_api.dto.response.feedback;

import com.example.learning_api.entity.sql.database.TeacherEntity;
import lombok.Data;

@Data
public class FeedbackAnswerResponse {
    private String id;
    private String testFeedbackId;
    private String answer;
    private String teacherId;
    private TeacherEntity teacher;
    private String createdAt;
    private String updatedAt;
}
