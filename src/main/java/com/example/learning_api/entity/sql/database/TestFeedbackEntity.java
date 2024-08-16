package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.FaqSourceType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "test_feedback")
public class TestFeedbackEntity {
     private String id;
     private String testId;
     private String studentId;
     private List<String> sources;
     private String feedback;
     private String createdAt;
     private String updatedAt;
     @DBRef
     private List<TestFeedbackAnswerEntity> answers;
     @DBRef
     private StudentEntity student;
}
