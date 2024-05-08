package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "lessons")
public class LessonEntity {
    @Id
    private String id;
    private String sectionId;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}
