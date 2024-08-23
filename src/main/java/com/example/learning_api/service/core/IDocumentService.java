package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.dto.request.document.UpdateDocumentRequest;
import com.example.learning_api.entity.sql.database.BlockEntity;

public interface IDocumentService {
    void createDocument(CreateDocumentRequest body);
    void updateDocument( UpdateDocumentRequest body);
    void deleteDocument(String documentId);
    void createBlock(BlockEntity body);
    void updateBlock(BlockEntity body);
    void deleteBlock(String blockId);

}
