package com.example.learning_api.repository.database;

import com.example.learning_api.dto.common.TotalTestOfDayDto;
import com.example.learning_api.entity.sql.database.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public interface TestRepository extends MongoRepository<TestEntity, String> {
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'status': {$in: ['UPCOMING', 'ONGOING', 'FINISHED']}}")
    Page<TestEntity> findByNameContaining(String name, Pageable pageable);

    @Aggregation({
            "{ $lookup: { from: 'student_enrollments', let: { classroomId: '$classroomId' }, pipeline: [ { $match: { $expr: { $and: [ { $eq: ['$classroomId', '$$classroomId'] }, { $eq: ['$studentId', ?0] } ] } } } ], as: 'enrollments' } }",
            "{ $match: { $and: [ { 'enrollments': { $not: { $size: 0 } } }, { 'startTime': { $lte: ?1 } }, { 'endTime': { $gt: ?1 } }, { 'status': {$in: ['UPCOMING', 'ONGOING', 'FINISHED']} } ] } }",
            "{ $project: { _id: 1, name: 1, description: 1, duration: 1, classroomId: 1, teacherId: 1, source: 1, startTime: 1, endTime: 1, status: 1, createdAt: 1, updatedAt: 1 } }"
    })
    Slice<TestEntity> findTestInProgressByStudentId(String studentId, String currentTimestamp, Pageable pageable);

    @Aggregation({
            "{ $lookup: { from: 'student_enrollments', let: { classroomId: '$classroomId' }, pipeline: [ { $match: { $expr: { $and: [ { $eq: ['$classroomId', '$$classroomId'] }, { $eq: ['$studentId', ?0] } ] } } } ], as: 'enrollments' } }",
            "{ $match: { $and: [ { 'enrollments': { $not: { $size: 0 } } }, { 'startTime': { $gte: ?1 } }, { 'startTime': { $lt: { $add: [?1, 86400000] } } }, { 'status': {$in: ['UPCOMING', 'ONGOING', 'FINISHED']} } ] } }",
            "{ $project: { _id: 1, name: 1, description: 1, duration: 1, classroomId: 1, teacherId: 1, source: 1, startTime: 1, endTime: 1, status: 1, createdAt: 1, updatedAt: 1, showResultType: 1 } }",
            "{ $sort: { 'startTime': 1 } }"
    })
    Slice<TestEntity> findTestsOnSpecificDateByStudentId(
            String studentId,
            String dateTimestamp,
            Pageable pageable
    );

    @Aggregation(pipeline = {
            "{ $addFields: { 'dayOfWeek': { $let: { vars: { 'days': [ 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday' ] }, in: { $arrayElemAt: [ '$$days', { $subtract: [ { $dayOfWeek: { $toDate: '$startTime' } }, 1 ] } ] } } } } }",
            "{ $match: { 'startTime': { $gte: { $toDate: ?0 }, $lte: { $toDate: ?1 } } } }",
            "{ $group: { _id: '$dayOfWeek', count: { $sum: 1 } } }",
            "{ $sort: { '_id': 1 } }"
    })
    List<TotalTestOfDayDto> findTestsInWeek(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String startOfWeek,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String endOfWeek
    );
    @Query("{'classroomId': ?0, 'status': {$in: ['UPCOMING', 'ONGOING', 'FINISHED']}}")
    Page<TestEntity> findByClassroomId(String classroomId, Pageable pageable);
    List<TestEntity> findByClassroomId(String classroomId);
    @Query("{'classroomId': ?0}")
    Page<TestEntity> findByClassroomIdAndStatus(String classroomId, Pageable pageable);

    @Query("{'classroomId': ?0}")
    List<TestEntity> findAllByClassroomId(String classroomId);
}