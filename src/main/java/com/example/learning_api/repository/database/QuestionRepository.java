package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuestionRepository extends MongoRepository<QuestionEntity, String>{
    @Query(value = "{'testId': ?0}", sort = "{'index': 1}")
    Page<QuestionEntity> findByTestId(String search, Pageable pageable);
    void deleteByTestId(String testId);
    @Query("{'testId': ?0}")
    List<QuestionEntity> findByTestId(String testId,org.springframework.data.domain.Sort sort);

    int countByTestId(String testId);
}
