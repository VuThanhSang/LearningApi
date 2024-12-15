package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.SubstanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubstanceRepository extends MongoRepository<SubstanceEntity, String> {
    Page<SubstanceEntity> findByLessonId(String lessonId, Pageable pageable);
    void deleteByLessonId(String lessonId);
}
