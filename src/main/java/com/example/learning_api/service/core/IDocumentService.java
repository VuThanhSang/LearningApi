package com.example.learning_api.service.core;

import java.util.List;

import com.example.learning_api.dto.request.document.AddPermissionRequest;
import com.example.learning_api.dto.request.document.CreateBlocksRequest;
import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.dto.request.document.UpdateDocumentRequest;
import com.example.learning_api.dto.response.document.GetDocumentDetailResponse;
import com.example.learning_api.entity.sql.database.BlockEntity;
import com.example.learning_api.entity.sql.database.DocumentEntity;
import com.example.learning_api.entity.sql.database.DocumentPermissionEntity;

public interface IDocumentService {
    void createDocument(CreateDocumentRequest body);

    void updateDocument(UpdateDocumentRequest body);

    void deleteDocument(String documentId);

    List<DocumentEntity> getDocuments(String search, int page, int size);
    GetDocumentDetailResponse getDocumentDetail(String documentId);
    void createBlock(CreateBlocksRequest body);

    void updateBlock(BlockEntity body,String userId);

    void deleteBlock(String blockId, String userId);


    void addPermission(AddPermissionRequest body);
    void removePermission(String documentId, String userId);
    List<DocumentPermissionEntity> getPermissions(String documentId);
}
