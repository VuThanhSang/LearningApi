package com.example.learning_api.entity.sql.database;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "feedback_answer")
public class FeedbackAnswerEntity {
    private String id;
    private String feedbackId;
    private String answer;
    private String teacherId;
    private String createdAt;
    private String updatedAt;
    @DBRef
    private TeacherEntity teacher;
}
