package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DeadlineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeadlineRepository extends MongoRepository<DeadlineEntity, String>{
    Page<DeadlineEntity> findAllByLessonId(String lessonId, Pageable pageable);
}
