package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CommentRepository extends MongoRepository<CommentEntity, String> {
    @Query("{'faqId': ?0}")
    Page<CommentEntity> findByFaqId(String faqId, Pageable pageable);
}
