package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public interface TestRepository extends MongoRepository<TestEntity, String> {
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<TestEntity> findByNameContaining(String name, Pageable pageable);

    @Aggregation({
            "{ $lookup: { from: 'student_enrollments', let: { courseId: '$courseId' }, pipeline: [ { $match: { $expr: { $and: [ { $eq: ['$courseId', '$$courseId'] }, { $eq: ['$studentId', ?0] } ] } } } ], as: 'enrollments' } }",
            "{ $match: { $and: [ { 'enrollments': { $not: { $size: 0 } } }, { 'endTime': { $gt: new Date() } } ] } }",
            "{ $project: { _id: 1, name: 1, description: 1, duration: 1, courseId: 1, source: 1, startTime: 1, endTime: 1, createdAt: 1, updatedAt: 1 } }"
    })
    Slice<TestEntity> findTestInProgressByStudentId(String studentId, Pageable pageable);

    @Aggregation({
            "{ $addFields: { startDateOnly: { $dateToString: { format: '%Y-%m-%d', date: '$startTime' } } } }",
            "{ $lookup: { from: 'student_enrollments', let: { courseId: '$courseId' }, pipeline: [ { $match: { $expr: { $and: [ { $eq: ['$courseId', '$$courseId'] }, { $eq: ['$studentId', ?0] } ] } } } ], as: 'enrollments' } }",
            "{ $match: { $and: [ { 'enrollments': { $not: { $size: 0 } } }, { 'startDateOnly': ?1 } ] } }",
            "{ $project: { _id: 1, name: 1, description: 1, duration: 1, courseId: 1, source: 1, startTime: 1, endTime: 1, createdAt: 1, updatedAt: 1 } }",
            "{ $sort: { 'startTime': 1 } }"
    })
    Slice<TestEntity> findTestsOnSpecificDateByStudentId(
            String studentId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date,
            Pageable pageable
    );
}