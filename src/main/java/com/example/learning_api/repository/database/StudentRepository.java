package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.StudentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<StudentEntity, String> {
}
