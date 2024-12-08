package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.SubstanceStatus;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "substances")
public class SubstanceEntity {
    @Id
    private String id;
    private String name;
    private String lessonId;
    private String content;
    private SubstanceStatus status;
    private String createdAt;
    private String updatedAt;
}
