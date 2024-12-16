package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<ReviewEntity, String> {
    Page<ReviewEntity> findByClassroomIdAndRating(String classroomId, Integer rating, Pageable pageable);
    Page<ReviewEntity> findByClassroomId(String classroomId, Pageable pageable);
    List<ReviewEntity> findByClassroomId(String classroomId);
}