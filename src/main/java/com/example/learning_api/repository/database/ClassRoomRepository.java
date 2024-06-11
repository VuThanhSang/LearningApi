package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.classroom.GetScheduleResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ClassRoomRepository extends MongoRepository<ClassRoomEntity, String> {
    @Query("{ 'name' : { $regex: ?0, $options: 'i' } }")
    Page<ClassRoomEntity> findByNameContaining(String search, Pageable pageable);

    @Query("{ 'courseId' : ?0 }")
    Page<ClassRoomEntity> findByCourseId(String courseId, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { 'sessions.dayOfWeek': ?0 } }",
            "{ $addFields: { daySpecificSessions: { $filter: { input: '$sessions', as: 'session', cond: { $eq: ['$$session.dayOfWeek', ?0] } } } } }",
            "{ $lookup: { from: 'student_enrollments', let: { classroomId: { $toString: '$_id' } }, pipeline: [{ $match: { $expr: { $and: [{ $eq: ['$studentId', ?1] }, { $eq: ['$classroomId', '$$classroomId'] }] } } }], as: 'enrollment' } }",
            "{ $project: { '_id': 1, 'name': 1, 'description': 1, 'image': 1, 'teacherId': 1, 'courseId': 1, 'sessions': '$daySpecificSessions', 'enrollment': { $arrayElemAt: ['$enrollment', 0] } } }"
    })
    List<ClassRoomEntity> findStudentScheduleByDayAndStudentId(String dayOfWeek, String studentId);

    @Aggregation(pipeline = {
            "{ $addFields: { id: { $toString: '$_id' } } }",
            "{ $lookup: { from: 'student_enrollments', localField: 'id', foreignField: 'classroomId', as: 'enrollments' } }",
            "{ $unwind: '$enrollments' }",
            "{ $match: { 'enrollments.studentId': 66350b7eafdf6323c9c962d4 } }",
            "{ $project: { _id: 0, className: '$name', courseId: '$courseId', classroomId: '$_id', sessions: '$sessions' } }",
            "{ $unwind: '$sessions' }",
            "{ $group: { _id: { dayOfWeek: '$sessions.dayOfWeek', className: '$className' }, sessions: { $push: { startTime: '$sessions.startTime', endTime: '$sessions.endTime' } } } }",
            "{ $group: { _id: null, schedule: { $push: { day: '$_id.dayOfWeek', className: '$_id.className', sessions: '$sessions' } } } }",
            "{ $project: { _id: 0, schedule: 1 } }"
    })
    Document getStudentSchedule(String studentId);
}