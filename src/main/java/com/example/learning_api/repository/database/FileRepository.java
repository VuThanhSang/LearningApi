package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FileRepository extends MongoRepository<FileEntity, String> {
    void deleteByOwnerIdAndOwnerType(String ownerId, String ownerType);
    List<FileEntity> findByOwnerIdAndOwnerType(String ownerId, String ownerType);
    @Query("{ 'ownerId': { $in: ?0 }, 'ownerType': ?1 }")
    List<FileEntity> findFilesByOwnerIdsAndType(List<String> ownerIds, String ownerType);
}
