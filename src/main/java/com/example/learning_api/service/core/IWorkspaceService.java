package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.workspace.CreateWorkSpaceRequest;
import com.example.learning_api.dto.request.workspace.CreateWorkspaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceRequest;
import com.example.learning_api.enums.BlockType;
import com.example.learning_api.enums.WorkspaceRole;
import com.example.learning_api.enums.WorkspaceType;

public interface IWorkspaceService {
    void createWorkspace(CreateWorkSpaceRequest body);
    void updateWorkspace(UpdateWorkSpaceRequest body);
    void deleteWorkspace(String workspaceId);
    void addUserToWorkspace(CreateWorkspaceMemberRequest body);
    void removeUserFromWorkspace(String workspaceId, String memberId);
    void updateWorkspaceMemberRole(UpdateWorkSpaceMemberRequest body);
}
