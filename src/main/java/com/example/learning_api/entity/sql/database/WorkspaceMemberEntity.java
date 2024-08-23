package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.WorkspaceMemberStatus;
import com.example.learning_api.enums.WorkspaceRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "workspace_members")
public class WorkspaceMemberEntity {
    private String id;
    private String workspaceId;
    private String memberId;
    private RoleEnum memberRole;
    private WorkspaceRole role;
    private WorkspaceMemberStatus status;
    private String createdAt;
    private String updatedAt;
}
