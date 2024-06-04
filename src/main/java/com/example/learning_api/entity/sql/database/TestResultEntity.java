package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "test_results")
public class TestResultEntity {
    @Id
    private String id;
    private String studentId;
    private int grade;
    private String testId;
    private String testType;
    private boolean isPassed;
    private Date attendedAt;
    private Date createdAt;
    private Date updatedAt;

}
