package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.WorkspaceType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "workspaces")
public class WorkspaceEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private RoleEnum ownerRole;
    private WorkspaceType type;
    private String createdAt;
    private String updatedAt;
    @DBRef
    private List<DocumentEntity> documents;

}
