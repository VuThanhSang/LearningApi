package com.example.learning_api.entity.sql.database;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "reviews")
public class ReviewEntity {
    private String id;
    private String userId;
    private String title;
    private String classroomId;
    private String content;
    private Double rating;
    private String createdAt;
    private String updatedAt;
}
