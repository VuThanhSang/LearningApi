package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.CourseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<CourseEntity, String>{
}
