package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.SubjectSpecializationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubjectSpecializationRepository extends MongoRepository<SubjectSpecializationEntity, String> {
    List<SubjectSpecializationEntity> findAllByTeacherId(String teacherId);
}
