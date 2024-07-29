package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "answers")
public class AnswerEntity {
    @Id
    private String id;
    @DBRef
    private QuestionEntity question;
    private String content;
    private boolean isCorrect;
    private String questionId;
    private String source;
    private String createdAt;
    private String updatedAt;
}
