package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.classroom.RecentClassDTO;
import com.example.learning_api.entity.sql.database.RecentClassEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RecentClassRepository extends MongoRepository<RecentClassEntity, String> {
    RecentClassEntity findByStudentIdAndClassroomId(String studentId, String classroomId);
    RecentClassEntity findByTeacherIdAndClassroomId(String teacherId, String classroomId);
    @Aggregation(pipeline = {
            "{ $addFields: { _classroomId: { $toObjectId: '$classroomId' } } }",
            "{ $lookup: { from: 'classrooms', localField: '_classroomId', foreignField: '_id', as: 'classInfo' } }",
            "{ $unwind: '$classInfo' }",
            "{ $match: { teacherId: ?0, 'classInfo.status': { $ne: 'BLOCKED' } } }",
            "{ $sort: { lastAccessedAt: -1 } }",
            "{ $project: { " +
                    "_id: 0, " +
                    "classId: '$classInfo._id', " +
                    "className: '$classInfo.name', " +
                    "description: '$classInfo.description', " +
                    "image: '$classInfo.image', " +
                    "courseId: '$classInfo.courseId', " +
                    "facultyId: '$classInfo.facultyId', " +
                    "enrollmentCapacity: '$classInfo.enrollmentCapacity', " +
                    "currentEnrollment: '$classInfo.currentEnrollment', " +
                    "status: '$classInfo.status', " +
                    "credits: '$classInfo.credits', " +
                    "termId: '$classInfo.termId', " +
                    "lastAccessedAt: 1 " +
                    "} }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    List<RecentClassDTO> findRecentClassesByTeacherId(String teacherId, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $addFields: { _classroomId: { $toObjectId: '$classroomId' } } }",
            "{ $lookup: { from: 'classrooms', localField: '_classroomId', foreignField: '_id', as: 'classInfo' } }",
            "{ $unwind: '$classInfo' }",
            "{ $match: { studentId: ?0, 'classInfo.status': { $ne: 'BLOCKED' } } }",
            "{ $sort: { lastAccessedAt: -1 } }",
            "{ $project: { " +
                    "_id: 0, " +
                    "classId: '$classInfo._id', " +
                    "className: '$classInfo.name', " +
                    "description: '$classInfo.description', " +
                    "image: '$classInfo.image', " +
                    "courseId: '$classInfo.courseId', " +
                    "facultyId: '$classInfo.facultyId', " +
                    "enrollmentCapacity: '$classInfo.enrollmentCapacity', " +
                    "currentEnrollment: '$classInfo.currentEnrollment', " +
                    "status: '$classInfo.status', " +
                    "credits: '$classInfo.credits', " +
                    "termId: '$classInfo.termId', " +
                    "lastAccessedAt: 1 " +
                    "} }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    List<RecentClassDTO> findRecentClassesByStudentId(String studentId, int skip, int limit);
    @Query(value = "{ 'teacherId': ?0 }", count = true)
    long countRecentClassesByTeacherId(String teacherId);
    long countRecentClassesByStudentId(String studentId);
}