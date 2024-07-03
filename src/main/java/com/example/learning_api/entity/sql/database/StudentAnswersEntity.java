package com.example.learning_api.entity.sql.database;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "student_answers")
public class StudentAnswersEntity {
    private String id;
    private String studentId;
    private String questionId;
    private String answerId;
    private String testId;
    private String testType;
    private boolean isCorrect;
    private Date createdAt;
    private Date updatedAt;
}
