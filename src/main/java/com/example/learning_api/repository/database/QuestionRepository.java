package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuestionRepository extends MongoRepository<QuestionEntity, String>{
    @Query("{'testId': ?0}")
    Page<QuestionEntity> findByTestId(String search, Pageable pageable);
    void deleteByTestId(String testId);
    List<QuestionEntity> findByTestId(String testId);

    int countByTestId(String testId);
}
