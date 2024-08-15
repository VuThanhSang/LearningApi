package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TestFeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TestFeedbackRepository extends MongoRepository<TestFeedbackEntity, String> {
    List<TestFeedbackEntity> findByStudentIdAndTestId(String studentId, String testId);
    Page<TestFeedbackEntity> findByTestId(String testId, Pageable pageable);
}
