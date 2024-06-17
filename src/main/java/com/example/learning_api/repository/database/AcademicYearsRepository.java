package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.AcademicYearsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AcademicYearsRepository extends MongoRepository<AcademicYearsEntity, String> {
}
