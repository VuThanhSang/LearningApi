package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface StudentRepository extends MongoRepository<StudentEntity, String> {
    @Query("{'user.fullname': {$regex: ?0, $options: 'i'}}")
    Page<StudentEntity> findByNameContaining(String email, Pageable pageable);
    StudentEntity findByUserId(String userId);
    @Query("{'user.email': ?0}")
    StudentEntity findByEmail(String email);
    @Query("{'classroomId': ?0}")
    List<StudentEntity> findByClassroomId(String classroomId);
    @Query("{ '_id': { $in: ?0 }, $or: [ " +
            "{ 'studentCode': { $regex: ?1, $options: 'i' } }, " +
            "{ 'user.fullname': { $regex: ?1, $options: 'i' } } " +
            "]}")
    List<StudentEntity> findByIdInAndSearch(List<String> studentIds, String search);
    @Query(value = "{" +
            "'_id': { $nin: ?0 }," +
            "$or: [" +
            "  { 'user.email': { $regex: ?1, $options: 'i' } }," +
            "  { 'user.fullname': { $regex: ?1, $options: 'i' } }" +
            "]" +
            "}")
    List<StudentEntity> findStudentsNotInClassroom(List<String> enrolledStudentIds, String search);

    @Query(value = "{" +
            "'_id': { $nin: ?0 }" +
            "}")
    List<StudentEntity> findStudentsNotInClassroom(List<String> enrolledStudentIds);

}
