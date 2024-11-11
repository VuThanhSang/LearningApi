package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.SectionStatus;
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
    private SectionStatus status;
    private Integer index;
    private String createdAt;
    private String updatedAt;
}
