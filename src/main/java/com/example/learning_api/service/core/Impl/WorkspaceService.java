package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.workspace.CreateWorkSpaceRequest;
import com.example.learning_api.dto.request.workspace.CreateWorkspaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceRequest;
import com.example.learning_api.entity.sql.database.WorkspaceEntity;
import com.example.learning_api.entity.sql.database.WorkspaceMemberEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.WorkspaceRole;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.repository.database.WorkspaceMemberRepository;
import com.example.learning_api.repository.database.WorkspaceRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IWorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceService implements IWorkspaceService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public void createWorkspace(CreateWorkSpaceRequest body) {
        try {
            if (body.getOwnerRole() == null)
                throw new Exception("Owner type is required");
            if (body.getOwnerRole().equals(RoleEnum.USER))
                studentRepository.findById(body.getOwnerId()).orElseThrow(() -> new Exception("Student not found"));
            else
                teacherRepository.findById(body.getOwnerId()).orElseThrow(() -> new Exception("Teacher not found"));
            WorkspaceEntity workspaceEntity = modelMapperService.mapClass(body, WorkspaceEntity.class);
            workspaceEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            workspaceEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            workspaceRepository.save(workspaceEntity);
            CreateWorkspaceMemberRequest createWorkspaceMemberRequest = new CreateWorkspaceMemberRequest();
            createWorkspaceMemberRequest.setMemberId(body.getOwnerId());
            createWorkspaceMemberRequest.setRole(WorkspaceRole.OWNER);
            createWorkspaceMemberRequest.setWorkspaceId(workspaceEntity.getId());
            createWorkspaceMemberRequest.setMemberRole(body.getOwnerRole());
            addUserToWorkspace(createWorkspaceMemberRequest);
        } catch (Exception e) {
            log.error("Error while creating workspace: {}", e.getMessage());
        }

    }

    @Override
    public void updateWorkspace(UpdateWorkSpaceRequest body) {
        try {
            if (body.getId() == null)
                throw new Exception("Workspace id is required");
            WorkspaceEntity workspaceEntity = workspaceRepository.findById(body.getId())
                    .orElseThrow(() -> new Exception("Workspace not found"));
            if (body.getName() != null)
                workspaceEntity.setName(body.getName());
            if (body.getDescription() != null)
                workspaceEntity.setDescription(body.getDescription());
            if (body.getType() != null)
                workspaceEntity.setType(body.getType());
            workspaceEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            workspaceRepository.save(workspaceEntity);
        } catch (Exception e) {
            log.error("Error while updating workspace: {}", e.getMessage());
        }

    }

    @Override
    public void deleteWorkspace(String workspaceId) {
        try {
            if (workspaceId == null)
                throw new Exception("Workspace id is required");
            if (workspaceRepository.findById(workspaceId).isEmpty())
                throw new Exception("Workspace not found");
            workspaceRepository.deleteById(workspaceId);
        } catch (Exception e) {
            log.error("Error while deleting workspace: {}", e.getMessage());
        }

    }

    @Override
    public void addUserToWorkspace(CreateWorkspaceMemberRequest body) {
        try {
            if (body.getWorkspaceId() == null)
                throw new Exception("Workspace id is required");
            if (workspaceRepository.findById(body.getWorkspaceId()).isEmpty())
                throw new Exception("Workspace not found");
            if (body.getMemberId() == null)
                throw new Exception("Member id is required");
            if (body.getRole() == null)
                throw new Exception("Role is required");
            if (body.getMemberRole().equals(RoleEnum.USER))
                studentRepository.findById(body.getMemberId()).orElseThrow(() -> new Exception("Student not found"));
            else
                teacherRepository.findById(body.getMemberId()).orElseThrow(() -> new Exception("Teacher not found"));
            WorkspaceMemberEntity workspaceMemberEntity = modelMapperService.mapClass(body,
                    WorkspaceMemberEntity.class);
            workspaceMemberEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            workspaceMemberEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            workspaceMemberRepository.save(workspaceMemberEntity);
        } catch (Exception e) {
            log.error("Error while adding user to workspace: {}", e.getMessage());
        }
    }

    @Override
    public void removeUserFromWorkspace(String workspaceId, String memberId) {
        try {
            if (workspaceId == null)
                throw new Exception("Workspace id is required");
            if (workspaceRepository.findById(workspaceId).isEmpty())
                throw new Exception("Workspace not found");
            if (memberId == null)
                throw new Exception("Member id is required");
            if (workspaceMemberRepository.findByWorkspaceIdAndMemberId(workspaceId, memberId) != null)
                throw new Exception("Member not found in workspace");
            workspaceMemberRepository.deleteByWorkspaceIdAndMemberId(workspaceId, memberId);
        } catch (Exception e) {
            log.error("Error while removing user from workspace: {}", e.getMessage());
        }

    }

    @Override
    public void updateWorkspaceMemberRole(UpdateWorkSpaceMemberRequest body) {
        try {

        } catch (Exception e) {
            log.error("Error while updating workspace member role: {}", e.getMessage());
        }
    }
}
