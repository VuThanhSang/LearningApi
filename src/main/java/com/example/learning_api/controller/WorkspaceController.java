package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.workspace.CreateWorkSpaceRequest;
import com.example.learning_api.dto.request.workspace.CreateWorkspaceMemberRequest;
import com.example.learning_api.dto.request.workspace.UpdateWorkSpaceRequest;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IWorkspaceService;
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




    @DeleteMapping(path = "/{id}/member/{memberId}")
    public ResponseEntity<ResponseAPI<String>> removeUserFromWorkspace(@PathVariable("id") String id, @PathVariable("memberId") String memberId){
        try{
            workspaceService.removeUserFromWorkspace(id, memberId);
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

}
