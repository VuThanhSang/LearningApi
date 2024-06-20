package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.classroom.GetScheduleResponse;
import com.example.learning_api.dto.response.classroom.TotalClassroomOfDayDto;
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
            "{ $unwind: '$sessions' }",
            "{ $group: { _id: '$sessions.dayOfWeek', count: { $sum: 1 } } }"
    })
    List<TotalClassroomOfDayDto> countSessionsByDayOfWeek();


}