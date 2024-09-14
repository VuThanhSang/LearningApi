package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DocumentEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {

    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Page<DocumentEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
