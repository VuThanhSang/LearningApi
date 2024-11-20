package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.MediaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaRepository extends MongoRepository<MediaEntity, String>{
    Page<MediaEntity> findByLessonId(String lessonId, Pageable pageable);
    Page<MediaEntity> findByClassroomId(String classroomId, Pageable pageable);
}
