package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileRepository extends MongoRepository<FileEntity, String> {
    void deleteByOwnerIdAndOwnerType(String ownerId, String ownerType);
    List<FileEntity> findByOwnerIdAndOwnerType(String ownerId, String ownerType);
}
