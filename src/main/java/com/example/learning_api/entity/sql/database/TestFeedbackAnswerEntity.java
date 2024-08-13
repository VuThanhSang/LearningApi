package com.example.learning_api.entity.sql.database;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "test_feedback_answer")
public class TestFeedbackAnswerEntity {
    private String id;
    private String testFeedbackId;
    private String answer;
    private String createdAt;
    private String updatedAt;
}
