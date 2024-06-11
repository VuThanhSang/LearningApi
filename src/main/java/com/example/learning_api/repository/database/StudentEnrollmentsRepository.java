package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.classroom.GetScheduleResponse;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StudentEnrollmentsRepository extends MongoRepository<StudentEnrollmentsEntity, String>{
    void deleteByStudentIdAndCourseId(String studentId, String courseId);
    @Query("{'studentId': ?0, 'courseId': ?1}")
    StudentEnrollmentsEntity findByStudentIdAndCourseId(String studentId, String courseId);

    @Aggregation(pipeline = {
            "{$addFields: {_classroomId: {$toObjectId: '$classroomId'}}}",
            "{$match: {studentId: '?0'}}",
            "{$lookup: {from: 'classrooms', localField: '_classroomId', foreignField: '_id', as: 'classroom'}}",
            "{$unwind: '$classroom'}",
            "{$unwind: '$classroom.sessions'}",
            "{$group: {_id: {dayOfWeek: '$classroom.sessions.dayOfWeek'}, sessions: {$push: {startTime: '$classroom.sessions.startTime', endTime: '$classroom.sessions.endTime', classroomId: '$classroom._id', className: '$classroom.name'}}}}",
            "{$unwind: '$sessions'}",
            "{$sort: {'sessions.startTime': 1}}",
            "{$group: {_id: '$_id.dayOfWeek', sessions: {$push: '$sessions'}}}",
            "{$project: {dayOfWeek: '$_id', sessions: 1, _id: 0}}"
    })
    AggregationResults<GetScheduleResponse> getWeeklySchedule(String studentId);
}
