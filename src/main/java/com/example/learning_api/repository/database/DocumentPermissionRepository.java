package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DocumentPermissionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentPermissionRepository extends MongoRepository<DocumentPermissionEntity, String> {
    DocumentPermissionEntity findByDocumentIdAndUserId(String documentId, String userId);
    DocumentPermissionEntity findByDocumentIdAndGrantedBy(String documentId, String grantedBy);
}
