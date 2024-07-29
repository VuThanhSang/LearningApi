package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.TestState;
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
    private double grade;
    private String testId;
    private boolean isPassed;
    private String attendedAt;
    private String finishedAt;
    private TestState state;
    private String createdAt;
    private String updatedAt;

}
