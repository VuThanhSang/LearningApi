package com.example.learning_api.entity.sql.database;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "document_comments")
public class DocumentCommentEntity {
    @Id
    private String id;
    private String blockId;
    private String userId;
    private String content;
    private String createdAt;
    private String updatedAt;
}
