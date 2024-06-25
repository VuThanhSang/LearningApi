package com.example.learning_api.controller;

import com.example.learning_api.dto.request.resource.CreateResourceRequest;
import com.example.learning_api.dto.request.resource.UpdateResourceRequest;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.example.learning_api.constant.RouterConstant.MEDIA_BASE_PATH;
import static com.example.learning_api.constant.RouterConstant.RESOURCE_BASE_PATH;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(RESOURCE_BASE_PATH)
public class ResourceController {
    private final IResourceService resourceService;
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> uploadResource(@ModelAttribute @Valid CreateResourceRequest body) {
        try{
            resourceService.createResource(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Upload resource successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }
    @PatchMapping(path = "/{resourceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateResource(@ModelAttribute @Valid UpdateResourceRequest body, @PathVariable String resourceId) {
        try{
            body.setId(resourceId);
            resourceService.updateResource(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update resource successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping(path = "/{resourceId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> deleteResource(@PathVariable String resourceId) {
        try{
            resourceService.deleteResource(resourceId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete resource successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

}
