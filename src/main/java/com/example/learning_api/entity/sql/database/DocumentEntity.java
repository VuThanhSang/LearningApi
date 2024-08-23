package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.ForumStatus;
import com.example.learning_api.enums.RoleEnum;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "documents")
public class DocumentEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private String workspaceId;
    private String ownerId;
    private RoleEnum ownerRole;
    private ForumStatus status;
    private String createdAt;
    private String updatedAt;

    @DBRef
    List<BlockEntity> blocks;
}
