package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.FaqCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FaqCommentRepository extends MongoRepository<FaqCommentEntity, String> {
    @Query("{'faqId': ?0}")
    Page<FaqCommentEntity> findByFaqId(String faqId, Pageable pageable);
}
