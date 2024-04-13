package com.example.learning_api.entity.sql.database;

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
    private String sectionId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
