package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackRepository extends MongoRepository<FeedbackEntity, String> {
    List<FeedbackEntity> findByStudentIdAndFormIdAndFormType(String studentId, String formId, String formType);
    Page<FeedbackEntity> findByFormIdAndFormType(String formId, String formType, Pageable pageable);
}
