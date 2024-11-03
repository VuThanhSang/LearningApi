package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.JoinClassRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
public interface JoinClassRequestRepository extends MongoRepository<JoinClassRequestEntity, String> {
    @Query("{ 'classroomId' : ?0, 'status' : 'PENDING' }")
    Page<JoinClassRequestEntity> findByClassroomId(String classroomId, Pageable pageable);
    Optional<JoinClassRequestEntity> findByClassroomIdAndStudentId(String classroomId, String studentId);
    @Query("{ 'classroomId' : ?0, 'student.email' : { $regex: ?1, $options: 'i' } }")
    Page<JoinClassRequestEntity> findByClassroomIdAndStudentEmailRegex(String classroomId, String email, Pageable pageable);
    @Query("{ 'classroomId' : ?0, 'student.name' : { $regex: ?1, $options: 'i' } }")
    Page<JoinClassRequestEntity> findByClassroomIdAndStudentNameRegex(String classroomId, String name, Pageable pageable);
    @Query("{ 'classroomId' : ?0, 'student.email' : { $regex: ?1, $options: 'i' }, 'student.name' : { $regex: ?2, $options: 'i' } }")
    Page<JoinClassRequestEntity> findByClassroomIdAndStudentEmailRegexAndStudentNameRegex(String classroomId, String email, String name, Pageable pageable);
}