package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.common.document.ContentBlock;
import com.example.learning_api.dto.request.document.AddPermissionRequest;
import com.example.learning_api.dto.request.document.CreateBlocksRequest;
import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.dto.request.document.UpdateDocumentRequest;
import com.example.learning_api.dto.response.document.GetDocumentDetailResponse;
import com.example.learning_api.entity.sql.database.BlockEntity;
import com.example.learning_api.entity.sql.database.DocumentEntity;
import com.example.learning_api.entity.sql.database.DocumentPermissionEntity;
import com.example.learning_api.enums.BlockType;
import com.example.learning_api.enums.PermissionDocumentType;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService implements IDocumentService {
    private final DocumentRepository documentRepository;
    private final ModelMapperService modelMapperService;
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final DocumentPermissionRepository documentPermissionRepository;
    @Override
    public void createDocument(CreateDocumentRequest body) {
        try {
            if (body.getName() == null)
                throw new Exception("Document name is required");
            if (body.getOwnerId() == null)
                throw new Exception("Owner id is required");

            DocumentEntity documentEntity = modelMapperService.mapClass(body, DocumentEntity.class);
            if (body.getIsNeedPermission() == null)
                documentEntity.setIsNeedPermission(false);
            documentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            documentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            documentRepository.save(documentEntity);
            DocumentPermissionEntity documentPermissionEntity = new DocumentPermissionEntity();
            documentPermissionEntity.setDocumentId(documentEntity.getId());
            documentPermissionEntity.setUserId(body.getOwnerId());
            documentPermissionEntity.setAccessLevel(PermissionDocumentType.OWNER);
            documentPermissionEntity.setGrantedBy(body.getOwnerId());
            documentPermissionRepository.save(documentPermissionEntity);
            // Split content into blocks and save each block
//            List<BlockEntity> blocks = splitContentIntoBlocks(content, documentEntity.getId());
//            blockRepository.saveAll(blocks);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating document: " + e.getMessage());
        }
    }
    @Override
    public void updateDocument(UpdateDocumentRequest body) {
        try {
            if (body.getId() == null)
                throw new Exception("Document id is required");
            DocumentEntity documentEntity = documentRepository.findById(body.getId())
                    .orElseThrow(() -> new Exception("Document not found"));
            if (body.getName() != null)
                documentEntity.setName(body.getName());
            if (body.getDescription() != null)
                documentEntity.setDescription(body.getDescription());
            if (body.getStatus() != null)
                documentEntity.setStatus(body.getStatus());
            if (body.getIsNeedPermission() != null)
                documentEntity.setIsNeedPermission(body.getIsNeedPermission());
            documentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            documentRepository.save(documentEntity);

        } catch (Exception e) {
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
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting document: " + e.getMessage());
        }
    }

    @Override
    public void createBlock(CreateBlocksRequest body) {
        try {
            if (body.getDocumentId() == null)
                throw new Exception("Document id is required");
            if (documentRepository.findById(body.getDocumentId()).isEmpty())
                throw new Exception("Document not found");
            for (CreateBlocksRequest.Block block : body.getBlocks()) {
                BlockEntity blockEntity = modelMapperService.mapClass(block, BlockEntity.class);
                blockEntity.setDocumentId(body.getDocumentId());
                blockEntity.setContent(block.getContent());
                blockEntity.setType(block.getType());
                if (blockRepository.findByDocumentId(body.getDocumentId()).isEmpty())
                    blockEntity.setIndex(0);
                else
                    blockEntity.setIndex(blockRepository.findByDocumentId(body.getDocumentId()).size());
                blockEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                blockEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                blockRepository.save(blockEntity);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while creating block: " + e.getMessage());
        }
    }

    @Override
    public void updateBlock(BlockEntity body, String userId) {
        try {
            if (body.getId() == null)
                throw new Exception("Block id is required");
            BlockEntity blockEntity = blockRepository.findById(body.getId())
                    .orElseThrow(() -> new Exception("Block not found"));
            DocumentEntity documentEntity = documentRepository.findById(blockEntity.getDocumentId())
                    .orElseThrow(() -> new Exception("Document not found"));
            if(!documentEntity.getIsNeedPermission()){
                if (body.getContent() != null)
                    blockEntity.setContent(body.getContent());
                if (body.getType() != null)
                    blockEntity.setType(body.getType());
                if (body.getIndex() != 0)
                    blockEntity.setIndex(body.getIndex());
                blockEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                blockRepository.save(blockEntity);
            }else{
                DocumentPermissionEntity documentPermissionEntity = documentPermissionRepository.findByDocumentIdAndUserId(blockEntity.getDocumentId(), userId);
                if (documentPermissionEntity == null)
                    throw new CustomException("You don't have permission to update this block");
                if (documentPermissionEntity.getAccessLevel() == PermissionDocumentType.OWNER || documentPermissionEntity.getAccessLevel() == PermissionDocumentType.EDIT) {
                    if (body.getContent() != null)
                        blockEntity.setContent(body.getContent());
                    if (body.getType() != null)
                        blockEntity.setType(body.getType());
                    if (body.getIndex() != 0)
                        blockEntity.setIndex(body.getIndex());
                    blockEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                    blockRepository.save(blockEntity);
                } else {
                    throw new CustomException("You don't have permission to update this block");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while updating block: " + e.getMessage());
        }

    }

    @Override
    public void deleteBlock(String blockId, String userId) {
        try {
            if (blockId == null)
                throw new Exception("Block id is required");
            BlockEntity blockEntity = blockRepository.findById(blockId)
                    .orElseThrow(() -> new Exception("Block not found"));
            DocumentEntity documentEntity = documentRepository.findById(blockEntity.getDocumentId())
                    .orElseThrow(() -> new Exception("Document not found"));
            if(!documentEntity.getIsNeedPermission()){
                blockRepository.deleteById(blockId);

            }
            else{
                DocumentPermissionEntity documentPermissionEntity = documentPermissionRepository.findByDocumentIdAndUserId(blockEntity.getDocumentId(), userId);
                if (documentPermissionEntity == null)
                    throw new CustomException("You don't have permission to delete this block");
                if (documentPermissionEntity.getAccessLevel() == PermissionDocumentType.OWNER || documentPermissionEntity.getAccessLevel() == PermissionDocumentType.EDIT) {
                    blockRepository.deleteById(blockId);
                } else {
                    throw new CustomException("You don't have permission to delete this block");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting block: " + e.getMessage());
        }

    }

    @Override
    public void addPermission(AddPermissionRequest body) {
        try {

            if (body.getDocumentId() == null)
                throw new Exception("Document id is required");

            if (documentRepository.findById(body.getDocumentId()).isEmpty())
                throw new Exception("Document not found");
            DocumentPermissionEntity author = documentPermissionRepository.findByDocumentIdAndUserId(body.getDocumentId(), body.getGrantedBy());
            if (author==null|| author.getAccessLevel() != PermissionDocumentType.OWNER)
                throw new Exception("You don't have permission to add permission");
            for (AddPermissionRequest.Permission user : body.getPermissions())
            {
                String userId = user.getUserId();
                DocumentPermissionEntity documentPermissionEntity = documentPermissionRepository.findByDocumentIdAndUserId(body.getDocumentId(), userId);
                if (documentPermissionEntity==null){
                    documentPermissionEntity = new DocumentPermissionEntity();

                }
                documentPermissionEntity.setAccessLevel(PermissionDocumentType.valueOf(user.getAccessLevel()));
                documentPermissionEntity.setDocumentId(body.getDocumentId());
                documentPermissionEntity.setUserId(userId);
                documentPermissionEntity.setGrantedBy(body.getGrantedBy());
                documentPermissionEntity.setGrantedAt(String.valueOf(System.currentTimeMillis()));
                documentPermissionEntity.setStatus("active");
                documentPermissionRepository.save(documentPermissionEntity);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while adding permission: " + e.getMessage());
        }
    }

    @Override
    public void removePermission(String documentId, String userId) {
        try {
            if (documentId == null)
                throw new Exception("Document id is required");
            if (userId == null)
                throw new Exception("User id is required");
            if (documentRepository.findById(documentId).isEmpty())
                throw new Exception("Document not found");
            if (userRepository.findById(userId).isEmpty())
                throw new Exception("User not found");
            DocumentPermissionEntity documentPermissionEntity = documentPermissionRepository.findByDocumentIdAndUserId(documentId, userId);
            if (documentPermissionEntity == null)
                throw new Exception("Permission not found");
            documentPermissionRepository.delete(documentPermissionEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error while removing permission: " + e.getMessage());
        }
    }



    @Override
    public List<DocumentPermissionEntity> getPermissions(String documentId) {
        try {
            if (documentId == null)
                throw new Exception("Document id is required");
            if (documentRepository.findById(documentId).isEmpty())
                throw new Exception("Document not found");
            return documentPermissionRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error while getting permissions: " + e.getMessage());
        }
    }

    @Override
    public List<DocumentEntity> getDocuments(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentEntity> documents = documentRepository.findByTitleContainingIgnoreCase(search, pageable);
        return documents.getContent();
    }

    @Override
    public GetDocumentDetailResponse getDocumentDetail(String documentId) {
        try {
            if (documentId == null)
                throw new Exception("Document id is required");
            DocumentEntity documentEntity = documentRepository.findById(documentId)
                    .orElseThrow(() -> new Exception("Document not found"));
            GetDocumentDetailResponse response = modelMapperService.mapClass(documentEntity, GetDocumentDetailResponse.class);
            response.setBlocks(blockRepository.findByDocumentId(documentId));
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while getting document detail: " + e.getMessage());
        }
    }
}
