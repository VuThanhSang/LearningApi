package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.dto.request.document.UpdateDocumentRequest;
import com.example.learning_api.entity.sql.database.BlockEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IDocumentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(DOCUMENT_BASE_PATH)
@Slf4j
public class DocumentController {
    private final IDocumentService documentService;

    @PostMapping(path = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createDocument(@ModelAttribute @Valid CreateDocumentRequest request){
        try{
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


    @PostMapping(path = "/{id}/document")
    public ResponseEntity<ResponseAPI<String>> createDocument(@PathVariable("id") String id, @RequestBody @Valid BlockEntity request){
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
    public ResponseEntity<ResponseAPI<String>> updateDocument( @PathVariable("blockId") String blockId, @RequestBody @Valid BlockEntity request){
        try{
            request.setId(blockId);
            documentService.updateBlock(request);
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
    public ResponseEntity<ResponseAPI<String>> deleteBlock(@PathVariable("blockId") String blockId){
        try{
            documentService.deleteBlock(blockId);
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
}