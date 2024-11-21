package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "media")
public class MediaEntity {
    @Id
    private String id;
    private String lessonId;
    private String url;
    private String fileType;
    private String fileName;
    private String fileSize;
    private String thumbnailPath;
    private Integer duration;
    private String classroomId;
    private String description;
    private String name;
    private String createdAt;
    private String updatedAt;
}
