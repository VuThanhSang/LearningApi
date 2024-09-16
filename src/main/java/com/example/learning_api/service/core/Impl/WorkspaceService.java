package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.workspace.CreateWorkSpaceRequest;
import com.example.learning_api.dto.request.workspace.CreateWorkspaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceRequest;
import com.example.learning_api.dto.response.workspace.GetWorkspaceMembersResponse;
import com.example.learning_api.dto.response.workspace.GetWorkspacesResponse;
import com.example.learning_api.entity.sql.database.WorkspaceEntity;
import com.example.learning_api.entity.sql.database.WorkspaceMemberEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.WorkspaceMemberStatus;
import com.example.learning_api.enums.WorkspaceRole;
import com.example.learning_api.enums.WorkspaceType;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.repository.database.WorkspaceMemberRepository;
import com.example.learning_api.repository.database.WorkspaceRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IWorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
                throw new IllegalArgumentException("Owner type is required");
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
            throw new IllegalArgumentException( e.getMessage());
        }

    }

    @Override
    public void updateWorkspace(UpdateWorkSpaceRequest body) {
        try {
            if (body.getId() == null)
                throw new IllegalArgumentException("Workspace id is required");
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
            throw new IllegalArgumentException( e.getMessage());
        }

    }

    @Override
    public void deleteWorkspace(String workspaceId) {
        try {
            if (workspaceId == null)
                throw new IllegalArgumentException("Workspace id is required");
            if (workspaceRepository.findById(workspaceId).isEmpty())
                throw new IllegalArgumentException("Workspace not found");
            workspaceRepository.deleteById(workspaceId);
        } catch (Exception e) {
            throw new IllegalArgumentException( e.getMessage());
        }

    }

    @Override
    public WorkspaceEntity getWorkspaceById(String workspaceId) {
        try {
            return workspaceRepository.findById(workspaceId).orElseThrow(() -> new Exception("Workspace not found"));
        } catch (Exception e) {
            throw new IllegalArgumentException( e.getMessage());
        }
    }

    @Override
    public GetWorkspacesResponse getWorkspaces(String search, int page, int size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<WorkspaceEntity> workspaceEntities = workspaceRepository.findByNameContaining(search, pageAble);
            return GetWorkspacesResponse.builder()
                    .totalPage(workspaceEntities.getTotalPages())
                    .totalElements(workspaceEntities.getTotalElements())
                    .workspaces(workspaceEntities.getContent())
                    .build();
        }
        catch (Exception e) {
            throw new IllegalArgumentException( e.getMessage());
        }
    }
    @Override
    public GetWorkspacesResponse getWorkspacesByUserId(String userId, String search, int page, int size) {
        try {
            int skip = page * size;
            List<WorkspaceEntity> workspaces = workspaceMemberRepository.findWorkspacesByMemberIdAndSearch(userId, search, skip, size);
            long totalElements = workspaceMemberRepository.countWorkspacesByMemberIdAndSearch(userId, search);
            int totalPages = (int) Math.ceil((double) totalElements / size);

            return GetWorkspacesResponse.builder()
                    .totalPage(totalPages)
                    .totalElements(totalElements)
                    .workspaces(workspaces)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetWorkspaceMembersResponse getWorkspaceMembers(String workspaceId, String search, int page, int size) {
        try {
            Pageable pageAble = PageRequest.of(page, size);
            Page<WorkspaceMemberEntity> workspaceMemberEntities = workspaceMemberRepository.findByWorkspaceId(workspaceId, pageAble);
            return GetWorkspaceMembersResponse.builder()
                    .totalPage(workspaceMemberEntities.getTotalPages())
                    .totalElements(workspaceMemberEntities.getTotalElements())
                    .members(workspaceMemberEntities.getContent())
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    @Override
    public void addUserToWorkspace(CreateWorkspaceMemberRequest body) {
        try {
            if (body.getWorkspaceId() == null)
                throw new IllegalArgumentException("Workspace id is required");
            if (workspaceRepository.findById(body.getWorkspaceId()).isEmpty())
                throw new IllegalArgumentException("Workspace not found");
            if (body.getMemberId() == null)
                throw new IllegalArgumentException("Member id is required");
            if (body.getRole() == null)
                throw new IllegalArgumentException("Role is required");
            if (body.getMemberRole().equals(RoleEnum.USER))
                studentRepository.findById(body.getMemberId()).orElseThrow(() -> new Exception("Student not found"));
            else
                teacherRepository.findById(body.getMemberId()).orElseThrow(() -> new Exception("Teacher not found"));
            WorkspaceMemberEntity exist = workspaceMemberRepository.findByWorkspaceIdAndMemberId(body.getWorkspaceId(), body.getMemberId());
            if (exist==null){
                WorkspaceMemberEntity workspaceMemberEntity = modelMapperService.mapClass(body,
                        WorkspaceMemberEntity.class);
                workspaceMemberEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                workspaceMemberEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                workspaceMemberRepository.save(workspaceMemberEntity);
            }
            else{
                if (exist.getStatus().equals(WorkspaceMemberStatus.LEAVE)){
                    exist.setStatus(WorkspaceMemberStatus.JOIN);
                    exist.setRole(body.getRole());
                    exist.setMemberRole(body.getMemberRole());
                    exist.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                    workspaceMemberRepository.save(exist);
                }
                else{
                    throw new IllegalArgumentException("User already in workspace");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException( e.getMessage());
        }
    }

    @Override
    public void removeUserFromWorkspace(String workspaceId, String memberId, String userId) {
        try {
            if (workspaceId == null)
                throw new IllegalArgumentException("Workspace id is required");
            if (workspaceRepository.findById(workspaceId).isEmpty())
                throw new IllegalArgumentException("Workspace not found");
            if (memberId == null)
                throw new IllegalArgumentException("Member id is required");
            WorkspaceMemberEntity workspaceMemberEntity = workspaceMemberRepository.findByWorkspaceIdAndMemberId(workspaceId, memberId);
            if (workspaceMemberEntity == null)
                throw new IllegalArgumentException("Member not found in workspace");
            WorkspaceEntity workspaceEntity = workspaceRepository.findById(workspaceId).orElseThrow(() -> new Exception("Workspace not found"));
            WorkspaceMemberEntity userMember = workspaceMemberRepository.findByWorkspaceIdAndMemberId(workspaceId, userId);
            if (workspaceEntity.getOwnerId().equals(userId)|| userMember.getRole() == WorkspaceRole.OWNER){
                if (workspaceMemberEntity.getRole().equals(WorkspaceRole.OWNER)){
                    throw new IllegalArgumentException("You are not allowed to remove this user because this user is owner of workspace");
                }
                workspaceMemberEntity.setStatus(WorkspaceMemberStatus.LEAVE);
                workspaceMemberEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                workspaceMemberRepository.save(workspaceMemberEntity);

            }else{
                throw new IllegalArgumentException("You are not allowed to remove this user");
            }


        } catch (Exception e) {
            throw new IllegalArgumentException( e.getMessage());
        }

    }

    @Override
    public void updateWorkspaceMemberRole(UpdateWorkSpaceMemberRequest body) {
        try {
            if (body.getWorkspaceId() == null)
                throw new IllegalArgumentException("Workspace id is required");
            if (workspaceRepository.findById(body.getWorkspaceId()).isEmpty())
                throw new IllegalArgumentException("Workspace not found");
            if (body.getMemberId() == null)
                throw new IllegalArgumentException("Member id is required");
            if (workspaceMemberRepository.findByWorkspaceIdAndMemberId(body.getWorkspaceId(), body.getMemberId()) == null)
                throw new IllegalArgumentException("Member not found in workspace");
            WorkspaceMemberEntity workspaceMemberEntity = workspaceMemberRepository.findByWorkspaceIdAndMemberId(body.getWorkspaceId(), body.getMemberId());
            if (body.getRole() != null)
                workspaceMemberEntity.setRole(body.getRole());
            if (body.getStatus() != null)
                workspaceMemberEntity.setStatus(body.getStatus());
            workspaceMemberEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            workspaceMemberRepository.save(workspaceMemberEntity);

        } catch (Exception e) {
            throw new IllegalArgumentException( e.getMessage());
        }
    }
}
