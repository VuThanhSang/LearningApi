package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.document.CreateDocumentRequest;

public interface IDocumentService {
    void createDocument(CreateDocumentRequest body);
    void updateDocument( CreateDocumentRequest body);
    void deleteDocument(String documentId);

}
