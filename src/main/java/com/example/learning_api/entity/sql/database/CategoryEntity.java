package com.example.learning_api.entity.sql.database;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "categories")
public class CategoryEntity {
    private String id;
    private String name;
    private String description;
    private Integer totalClassRoom;
    private String createdAt;
    private String updatedAt;
}
