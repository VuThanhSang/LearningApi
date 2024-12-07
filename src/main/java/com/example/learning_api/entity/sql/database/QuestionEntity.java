package com.example.learning_api.entity.sql.database;


import com.example.learning_api.enums.QuestionStatus;
import com.example.learning_api.enums.QuestionType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "questions")
public class QuestionEntity {
    @Id
    private String id;
    private String content;
    private String testId;
    private String description;
    private QuestionType type;
    private QuestionStatus status;
    private Integer index;
    private String createdAt;
    private String updatedAt;

    // New field for text and fill-in-the-blank questions
    private List<String> correctAnswers;

    private List<AnswerEntity> answers;
}
