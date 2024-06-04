package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public interface CourseRepository extends MongoRepository<CourseEntity, String>{
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<CourseEntity> findByNameContaining(String name, Pageable pageable);
    @Aggregation(pipeline = {
            "{ $match: { 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 } } }",
            "{ $lookup: { from: 'student_enrollments', let: { courseId: '$_id' }, pipeline: [{ $match: { $expr: { $eq: [{ $toObjectId: '$courseId' }, '$$courseId'] } } }], as: 'enrollments' } }",
            "{ $unwind: { path: '$enrollments', preserveNullAndEmptyArrays: false } }",
            "{ $match: { 'enrollments.studentId': ?1 } }",
            "{ $project: { '_id': 1, 'name': 1, 'description': 1, 'teacherId': 1, 'startDate': 1, 'endDate': 1, 'enrolledAt': '$enrollments.enrolledAt', 'grade': '$enrollments.grade', 'studentId': '$enrollments.studentId' } }"
    })
    List<CourseEntity> findCoursesInProgressByDateAndStudentId(Date date, String studentId);
}
