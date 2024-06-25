package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.RecentClassEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecentClassRepository extends MongoRepository<RecentClassEntity, String> {
    RecentClassEntity findByStudentIdAndClassroomId(String studentId, String classroomId);

}
