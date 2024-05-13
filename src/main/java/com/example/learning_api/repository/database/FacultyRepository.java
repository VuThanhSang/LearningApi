package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.FacultyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FacultyRepository extends MongoRepository<FacultyEntity, String> {
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<FacultyEntity> findByNameContaining(String name);
}
