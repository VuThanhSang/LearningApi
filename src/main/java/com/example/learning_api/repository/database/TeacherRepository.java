package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TeacherEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeacherRepository extends MongoRepository<TeacherEntity, String> {
}
