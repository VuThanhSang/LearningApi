package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.BlockType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "blocks")
public class BlockEntity {
    @Id
    private String id;
    private String documentId;
    private String content;
    private BlockType type;
    private int index;
    private String createdAt;
    private String updatedAt;
}
