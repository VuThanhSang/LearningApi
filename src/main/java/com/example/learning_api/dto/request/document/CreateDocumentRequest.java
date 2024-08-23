package com.example.learning_api.dto.request.document;

import com.example.learning_api.enums.ForumStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;

@Data
public class CreateDocumentRequest {
    private String name;
    private String description;
    private String workspaceId;
    private String ownerId;
    private RoleEnum ownerRole;
    private ForumStatus status;
}
