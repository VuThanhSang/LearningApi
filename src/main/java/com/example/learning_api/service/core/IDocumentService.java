package com.example.learning_api.service.core;

import java.util.List;

import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.dto.request.document.UpdateDocumentRequest;
import com.example.learning_api.entity.sql.database.BlockEntity;
import com.example.learning_api.entity.sql.database.DocumentEntity;

public interface IDocumentService {
    void createDocument(CreateDocumentRequest body);

    void updateDocument(UpdateDocumentRequest body);

    void deleteDocument(String documentId);

    List<DocumentEntity> getDocuments(String search, int page, int size);

    void createBlock(BlockEntity body);

    void updateBlock(BlockEntity body);

    void deleteBlock(String blockId);

}
