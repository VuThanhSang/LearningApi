package com.example.learning_api.dto.request.workspace;

import com.example.learning_api.enums.WorkspaceType;
import lombok.Data;

@Data
public class UpdateWorkSpaceRequest {
    private String id;
    private String name;
    private String description;
    private WorkspaceType type;

}
