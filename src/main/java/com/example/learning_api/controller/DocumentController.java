package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.document.AddPermissionRequest;
import com.example.learning_api.dto.request.document.CreateBlocksRequest;
import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.dto.request.document.UpdateDocumentRequest;
import com.example.learning_api.dto.response.document.GetDocumentDetailResponse;
import com.example.learning_api.entity.sql.database.BlockEntity;
import com.example.learning_api.entity.sql.database.DocumentPermissionEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IDocumentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(DOCUMENT_BASE_PATH)
@Slf4j
public class DocumentController {
    private final IDocumentService documentService;
    private final JwtService jwtService;
    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> createDocument(@RequestBody @Valid CreateDocumentRequest request,@RequestHeader("Authorization") String authorization){
        try{
            String token = authorization.substring(7);
            String userId = jwtService.extractUserId(token);
            request.setOwnerId(userId);
            documentService.createDocument(request);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create document successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            log.error("Error when create document: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<String>> updateDocument(@PathVariable("id") String id, @RequestBody @Valid UpdateDocumentRequest request){
        try{
            request.setId(id);
            documentService.updateDocument( request);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update document successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when update document: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<String>> deleteDocument(@PathVariable("id") String id){
        try{
            documentService.deleteDocument(id);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete document successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when delete document: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<GetDocumentDetailResponse>> getDocumentDetail(@PathVariable("id") String id){
        try{
            GetDocumentDetailResponse data= documentService.getDocumentDetail(id);
            ResponseAPI<GetDocumentDetailResponse> res = ResponseAPI.<GetDocumentDetailResponse>builder()
                    .timestamp(new Date())
                    .data(data)
                    .message("Get document detail successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when get document detail: ", e);
            ResponseAPI<GetDocumentDetailResponse> res = ResponseAPI.<GetDocumentDetailResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


    @PostMapping(path = "/{id}/block")
    public ResponseEntity<ResponseAPI<String>> createDocument(@PathVariable("id") String id, @RequestBody @Valid CreateBlocksRequest request){
        try{
            request.setDocumentId(id);
            documentService.createBlock(request);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create document successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            log.error("Error when create document: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/block/{blockId}")
    public ResponseEntity<ResponseAPI<String>> updateDocument( @PathVariable("blockId") String blockId, @RequestBody @Valid BlockEntity request, @RequestHeader("Authorization") String authorization){
        try{
            String token = authorization.substring(7);
            String userId = jwtService.extractUserId(token);
            request.setId(blockId);
            documentService.updateBlock(request, userId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update document successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when update document: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/block/{blockId}")
    public ResponseEntity<ResponseAPI<String>> deleteBlock(@PathVariable("blockId") String blockId, @RequestHeader("Authorization") String authorization){
        try{
            String token = authorization.substring(7);
            String userId = jwtService.extractUserId(token);
            documentService.deleteBlock(blockId,userId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete document successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when delete document: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


    @PostMapping(path = "/{documentId}/permission")
    public ResponseEntity<ResponseAPI<String>> addPermission(@PathVariable("documentId") String documentId, @RequestBody @Valid AddPermissionRequest request, @RequestHeader("Authorization") String authorization){
        try{
            String token = authorization.substring(7);
            String userId = jwtService.extractUserId(token);
            request.setDocumentId(documentId);
            request.setGrantedBy(userId);
            documentService.addPermission(request);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Add permission successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            log.error("Error when add permission: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @DeleteMapping(path = "/{documentId}/permission/{userId}")
    public ResponseEntity<ResponseAPI<String>> removePermission(@PathVariable("documentId") String documentId, @PathVariable("userId") String userId){
        try{
            documentService.removePermission(documentId, userId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Remove permission successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when remove permission: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @GetMapping(path = "/{documentId}/permission")
    public ResponseEntity<ResponseAPI<List<DocumentPermissionEntity>>> getPermissions(@PathVariable("documentId") String documentId){
        try{
            List<DocumentPermissionEntity> data= documentService.getPermissions(documentId);
            ResponseAPI<List<DocumentPermissionEntity>> res = ResponseAPI.<List<DocumentPermissionEntity>>builder()
                    .timestamp(new Date())
                    .data(data)
                    .message("Get permission successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error when get permission: ", e);
            ResponseAPI<List<DocumentPermissionEntity>> res = ResponseAPI.<List<DocumentPermissionEntity>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }



    }

}