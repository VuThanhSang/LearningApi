package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.TestShowResultType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "test")
public class TestEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private int duration;
    private String classroomId;
    private String createdBy;
    private String source;
    private TestShowResultType showResultType;
    private Date startTime;
    private Date endTime;
    private Date createdAt;
    private Date updatedAt;
    @DBRef
    private List<QuestionEntity> questions;
}
