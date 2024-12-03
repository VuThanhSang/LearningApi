package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.FAQEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FAQRepository extends MongoRepository<FAQEntity, String>{

    Page<FAQEntity> findByQuestionContainingIgnoreCase(String question, Pageable pageable);

    Page<FAQEntity> findByStatus(String status, Pageable pageable);

    @Query("{ $or: [ { 'question': { $regex: ?0, $options: 'i' } }, { 'subject': { $regex: ?0, $options: 'i' } } ] }")
    Page<FAQEntity> findByQuestionOrSubjectContainingIgnoreCaseAndStatus(String query, Pageable pageable);
}
