package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.dto.request.document.UpdateDocumentRequest;
import com.example.learning_api.entity.sql.database.BlockEntity;
import com.example.learning_api.entity.sql.database.DocumentEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.repository.database.*;
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
    private final BlockRepository blockRepository;

    @Override
    public void createDocument(CreateDocumentRequest body) {
        try{

            if (body.getWorkspaceId() == null)
                throw new Exception("Workspace id is required");
            if (workspaceRepository.findById(body.getWorkspaceId()).isEmpty())
                throw new Exception("Workspace not found");
            if (body.getOwnerRole() == null)
                throw new Exception("Owner role is required");
            if (body.getOwnerRole().equals(RoleEnum.USER))
                studentRepository.findById(body.getOwnerId()).orElseThrow(() -> new Exception("Student not found"));
            else
                teacherRepository.findById(body.getOwnerId()).orElseThrow(() -> new Exception("Teacher not found"));
            DocumentEntity documentEntity = modelMapperService.mapClass(body, DocumentEntity.class);
            documentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            documentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            documentRepository.save(documentEntity);
        }
        catch (Exception e){
            throw new RuntimeException("Error while creating document: " + e.getMessage());
        }


    }

    @Override
    public void updateDocument(UpdateDocumentRequest body) {
        try {
            if (body.getId() == null)
                throw new Exception("Document id is required");
            DocumentEntity documentEntity = documentRepository.findById(body.getId()).orElseThrow(() -> new Exception("Document not found"));
            if (body.getName() != null)
                documentEntity.setName(body.getName());
            if (body.getDescription() != null)
                documentEntity.setDescription(body.getDescription());
            if (body.getStatus() != null)
                documentEntity.setStatus(body.getStatus());
            documentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            documentRepository.save(documentEntity);

        }
        catch (Exception e){
            throw new RuntimeException("Error while updating document: " + e.getMessage());
        }


    }

    @Override
    public void deleteDocument(String documentId) {
        try {
            if (documentId == null)
                throw new Exception("Document id is required");
            if (documentRepository.findById(documentId).isEmpty())
                throw new Exception("Document not found");
            documentRepository.deleteById(documentId);
        }
        catch (Exception e){
            throw new RuntimeException("Error while deleting document: " + e.getMessage());
        }
    }

    @Override
    public void createBlock(BlockEntity body) {
        try {
            if (body.getDocumentId() == null)
                throw new Exception("Document id is required");
            if (documentRepository.findById(body.getDocumentId()).isEmpty())
                throw new Exception("Document not found");
            BlockEntity blockEntity = modelMapperService.mapClass(body, BlockEntity.class);
            blockEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            blockEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            blockRepository.save(blockEntity);
        }
        catch (Exception e){
            throw new RuntimeException("Error while creating block: " + e.getMessage());
        }
    }

    @Override
    public void updateBlock(BlockEntity body) {
        try {
            if (body.getId() == null)
                throw new Exception("Block id is required");
            BlockEntity blockEntity = blockRepository.findById(body.getId()).orElseThrow(() -> new Exception("Block not found"));
            if (body.getContent() != null)
                blockEntity.setContent(body.getContent());
            if (body.getType() != null)
                blockEntity.setType(body.getType());
            blockEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            blockRepository.save(blockEntity);
        }
        catch (Exception e){
            throw new RuntimeException("Error while updating block: " + e.getMessage());
        }

    }

    @Override
    public void deleteBlock(String blockId) {
        try {
            if (blockId == null)
                throw new Exception("Block id is required");
            if (blockRepository.findById(blockId).isEmpty())
                throw new Exception("Block not found");
            blockRepository.deleteById(blockId);
        }
        catch (Exception e){
            throw new RuntimeException("Error while deleting block: " + e.getMessage());
        }

    }
}
