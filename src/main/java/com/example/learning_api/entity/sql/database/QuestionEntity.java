package com.example.learning_api.entity.sql.database;


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
    @DBRef
    private TestEntity test;
    private String questionText;
    private Date createdAt;
    private Date updatedAt;
    @DBRef
    private List<AnswerEntity> answers;
}
