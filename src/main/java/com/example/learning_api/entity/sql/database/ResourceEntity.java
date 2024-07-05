package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "resources")
public class ResourceEntity {
    @Id
    private String id;
    private String lessonId;
    private String name;
    private String description;
    private String filePath;
    private String fileType;
    private String fileExtension;
    private String fileName;
    private String FileSize;
    private Date createdAt;
    private Date updatedAt;
}
