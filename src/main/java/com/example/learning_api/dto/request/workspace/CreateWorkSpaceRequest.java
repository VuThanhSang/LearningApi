package com.example.learning_api.dto.request.workspace;

import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.WorkspaceType;
import lombok.Data;

@Data
public class CreateWorkSpaceRequest {
    private String name;
    private String description;
    private String ownerId;
    private RoleEnum ownerRole;
    private WorkspaceType type;
}
