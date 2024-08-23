package com.example.learning_api.dto.request.workspace;

import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.WorkspaceMemberStatus;
import com.example.learning_api.enums.WorkspaceRole;
import lombok.Data;

@Data
public class UpdateWorkSpaceMemberRequest {
    private String workspaceId;
    private String memberId;
    private RoleEnum memberRole;
    private WorkspaceRole role;
    private WorkspaceMemberStatus status;
}
