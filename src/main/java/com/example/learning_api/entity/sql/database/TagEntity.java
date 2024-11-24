package com.example.learning_api.entity.sql.database;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "tags")
public class TagEntity {
    private String id;
    private String name;
    private String classId;
    private Boolean isForClass;
    @Min(0)
    private int postCount;

    private String createdAt;
    private String updatedAt;
}
