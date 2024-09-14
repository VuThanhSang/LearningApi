package com.example.learning_api.entity.sql.database;


import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "scoring_criteria")
public class ScoringCriteriaEntity {
    @Id
    private String id;
    private String deadlineId;
    private String title;
    private String description;
    private String score;
    private String createdAt;
    private String updatedAt;
}
