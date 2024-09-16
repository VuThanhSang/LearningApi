package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.workspace.CreateWorkSpaceRequest;
import com.example.learning_api.dto.request.workspace.CreateWorkspaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceRequest;
import com.example.learning_api.dto.response.workspace.GetWorkspaceMembersResponse;
import com.example.learning_api.dto.response.workspace.GetWorkspacesResponse;
import com.example.learning_api.entity.sql.database.WorkspaceEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IWorkspaceService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(WORKSPACE_BASE_PATH)
@Slf4j
public class WorkspaceController {
    private final IWorkspaceService workspaceService;
    private final JwtService jwtService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> createWorkspace(@RequestBody @Valid CreateWorkSpaceRequest request){
        try{
            workspaceService.createWorkspace(request);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create workspace successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            log.error("Error when create workspace: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<String>> updateWorkspace(@PathVariable("id") String id, @RequestBody @Valid UpdateWorkSpaceRequest request){
        try{
            request.setId(id);
            workspaceService.updateWorkspace(request);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update workspace successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when update workspace: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<String>> deleteWorkspace(@PathVariable("id") String id){
        try{
            workspaceService.deleteWorkspace(id);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete workspace successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when delete workspace: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
   @GetMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<WorkspaceEntity>> getWorkspaceById(@PathVariable("id") String id){
        try{
            WorkspaceEntity data= workspaceService.getWorkspaceById(id);
            ResponseAPI<WorkspaceEntity> res = ResponseAPI.<WorkspaceEntity>builder()
                    .timestamp(new Date())
                    .message("Get workspace by id successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when get workspace by id: ", e);
            ResponseAPI<WorkspaceEntity> res = ResponseAPI.<WorkspaceEntity>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetWorkspacesResponse>> getWorkspaces(@RequestParam(value = "search", required = false,defaultValue = "") String search,
                                                                     @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                                     @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        try{
            GetWorkspacesResponse data= workspaceService.getWorkspaces(search, page-1, size);
            ResponseAPI<GetWorkspacesResponse> res = ResponseAPI.<GetWorkspacesResponse>builder()
                    .timestamp(new Date())
                    .message("Get workspaces successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when get workspaces: ", e);
            ResponseAPI<GetWorkspacesResponse> res = ResponseAPI.<GetWorkspacesResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<ResponseAPI<GetWorkspacesResponse>> getWorkspacesByUserId(@PathVariable("userId") String userId,
                                                                     @RequestParam(value = "search", required = false,defaultValue = "") String search,
                                                                     @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                                     @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        try{
            GetWorkspacesResponse data= workspaceService.getWorkspacesByUserId(userId, search, page-1, size);
            ResponseAPI<GetWorkspacesResponse> res = ResponseAPI.<GetWorkspacesResponse>builder()
                    .timestamp(new Date())
                    .message("Get workspaces by user id successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when get workspaces by user id: ", e);
            ResponseAPI<GetWorkspacesResponse> res = ResponseAPI.<GetWorkspacesResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/{id}/member")
    public ResponseEntity<ResponseAPI<String>> addUserToWorkspace(@PathVariable("id") String id, @RequestBody @Valid CreateWorkspaceMemberRequest request){
        try{
            request.setWorkspaceId(id);
            workspaceService.addUserToWorkspace(request);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Add user to workspace successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            log.error("Error when add user to workspace: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


    @PatchMapping(path = "/{workspaceId}/member/{memberId}")
    public ResponseEntity<ResponseAPI<String>> updateWorkspaceMemberRole(@RequestBody @Valid UpdateWorkSpaceMemberRequest request, @PathVariable("memberId") String memberId, @PathVariable("workspaceId") String workspaceId){
        try{
            request.setWorkspaceId(workspaceId);
            request.setMemberId(memberId);
            workspaceService.updateWorkspaceMemberRole(request);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update user role in workspace successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when update user role in workspace: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


    @DeleteMapping(path = "/{workspaceId}/member/{memberId}")
    public ResponseEntity<ResponseAPI<String>> removeUserFromWorkspace(@PathVariable("workspaceId") String workspaceId, @PathVariable("memberId") String memberId, HttpServletRequest request){
        try{
            String token = request.getHeader("Authorization");
            token = token.substring(7);
            String userId = jwtService.extractUserIdFromToken(token);
            workspaceService.removeUserFromWorkspace(workspaceId, memberId,userId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Remove user from workspace successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when remove user from workspace: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }



    }
    @GetMapping(path = "/{id}/member")
    public ResponseEntity<ResponseAPI<GetWorkspaceMembersResponse>> getWorkspaceMembers(@PathVariable("id") String id,
                                                                                        @RequestParam(value = "search", required = false,defaultValue = "") String search,
                                                                                        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                                                        @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        try{
            GetWorkspaceMembersResponse data= workspaceService.getWorkspaceMembers(id, search, page-1, size);
            ResponseAPI<GetWorkspaceMembersResponse> res = ResponseAPI.<GetWorkspaceMembersResponse>builder()
                    .timestamp(new Date())
                    .message("Get workspace members successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when get workspace members: ", e);
            ResponseAPI<GetWorkspaceMembersResponse> res = ResponseAPI.<GetWorkspaceMembersResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

}
