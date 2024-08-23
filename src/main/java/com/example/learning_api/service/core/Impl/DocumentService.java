package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.repository.database.DocumentRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.repository.database.WorkspaceRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService implements IDocumentService {
    private final DocumentRepository documentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public void createDocument(CreateDocumentRequest body) {


    }

    @Override
    public void updateDocument(CreateDocumentRequest body) {

    }

    @Override
    public void deleteDocument(String documentId) {

    }
}
