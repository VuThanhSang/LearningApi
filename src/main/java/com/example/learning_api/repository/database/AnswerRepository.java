package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.AnswerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AnswerRepository extends MongoRepository<AnswerEntity, String>{

    void deleteByQuestionId(String questionId);
    List<AnswerEntity> findByQuestionId(String questionId);
    @Query(value = "{questionId: ?0}", sort = "{index: -1}")
    Integer getIndexMaxByQuestionId(String questionId);
    @Query("{ 'questionId': { $in: ?0 } }")
    List<AnswerEntity> findByIdIn(List<String> ids);
    @Query("{ 'questionId': { $in: ?0 } }")
    List<AnswerEntity> findByQuestionIds(List<String> questionIds);
}
