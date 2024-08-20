package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FeedbackFormType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "feedback")
public class FeedbackEntity {
     private String id;
     private String formId;
     private FeedbackFormType formType;
     private String studentId;
     private String title;
     private String feedback;
     private String createdAt;
     private String updatedAt;
     @DBRef
     private List<FeedbackAnswerEntity> answers;
     @DBRef
     private StudentEntity student;
     @DBRef
     private List<FileEntity> files;
}
