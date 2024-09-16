package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.workspace.CreateWorkSpaceRequest;
import com.example.learning_api.dto.request.workspace.CreateWorkspaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceRequest;
import com.example.learning_api.dto.response.workspace.GetWorkspaceMembersResponse;
import com.example.learning_api.dto.response.workspace.GetWorkspacesResponse;
import com.example.learning_api.entity.sql.database.WorkspaceEntity;
import com.example.learning_api.enums.BlockType;
import com.example.learning_api.enums.WorkspaceRole;
import com.example.learning_api.enums.WorkspaceType;

public interface IWorkspaceService {
    void createWorkspace(CreateWorkSpaceRequest body);
    void updateWorkspace(UpdateWorkSpaceRequest body);
    void deleteWorkspace(String workspaceId);
    WorkspaceEntity getWorkspaceById(String workspaceId);
    GetWorkspacesResponse getWorkspaces(String search, int page, int size);
    GetWorkspacesResponse getWorkspacesByUserId(String userId, String search, int page, int size);
    GetWorkspaceMembersResponse getWorkspaceMembers(String workspaceId, String search, int page, int size);
    void addUserToWorkspace(CreateWorkspaceMemberRequest body);
    void removeUserFromWorkspace(String workspaceId, String memberId,String userId);
    void updateWorkspaceMemberRole(UpdateWorkSpaceMemberRequest body);
}
