package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StudentRepository extends MongoRepository<StudentEntity, String> {
    @Query("{'user.fullname': {$regex: ?0, $options: 'i'}}")
    Page<StudentEntity> findByNameContaining(String email, Pageable pageable);
    StudentEntity findByUserId(String userId);
    @Query("{'user.email': ?0}")
    StudentEntity findByEmail(String email);
}
