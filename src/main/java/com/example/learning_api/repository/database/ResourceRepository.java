package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResourceRepository extends MongoRepository<ResourceEntity, String> {
    Page<ResourceEntity> findByLessonId(String lessonId, Pageable pageable);
    void deleteByLessonId(String lessonId);
}
