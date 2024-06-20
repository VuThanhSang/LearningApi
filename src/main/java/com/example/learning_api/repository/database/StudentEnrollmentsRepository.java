package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.classroom.GetScheduleResponse;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface StudentEnrollmentsRepository extends MongoRepository<StudentEnrollmentsEntity, String>{
//    void deleteByStudentIdAndCourseId(String studentId, String courseId);
    @Query("{'studentId': ?0, 'courseId': ?1}")
    StudentEnrollmentsEntity findByStudentIdAndCourseId(String studentId, String courseId);

    @Query("{'studentId': ?0}")
    List<StudentEnrollmentsEntity> findByStudentId(String studentId);
    @Aggregation(pipeline = {
            "{$addFields: {_classroomId: {$toObjectId: '$classroomId'}}}",
            "{$match: {studentId: '?0'}}",
            "{$lookup: {from: 'classrooms', localField: '_classroomId', foreignField: '_id', as: 'classroom'}}",
            "{$unwind: '$classroom'}",
            "{$addFields: {'classroom.classroomId': {$toString: '$classroom._id'}}}",
            "{$lookup: {from: 'schedules', localField: 'classroom.classroomId', foreignField: 'classroomId', as: 'schedules'}}",
            "{$unwind: '$schedules'}",
            "{$group: {_id: {dayOfWeek: '$schedules.dayOfWeek'}, sessions: {$push: {startTime: '$schedules.startTime', endTime: '$schedules.endTime', className: '$classroom.name', classroomId: '$classroom.classroomId'}}}}",
            "{$project: {dayOfWeek: '$_id.dayOfWeek', sessions: 1, _id: 0}}",
            "{$sort: {dayOfWeek: 1}}"
    })
    AggregationResults<GetScheduleResponse> getWeeklySchedule(String studentId);
}
