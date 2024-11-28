package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.classroom.ClassroomDeadlineResponse;
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
    @Query("{ '_id' : { $in: ?0 }, 'name' : { $regex: ?1, $options: 'i' } }")
    Page<ClassRoomEntity> findByIdInAndNameContaining(List<String> classroomIds, String search, Pageable pageable);
    @Query("{ 'teacherId' : ?0, 'name' : { $regex: ?1, $options: 'i' } }")
    Page<ClassRoomEntity> findByTeacherIdAndNameContaining(String teacherId, String search, Pageable pageable);
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

    @Aggregation(pipeline = {
            "{ $match: { _id: ObjectId(?0) } }",
            "{ $lookup: { from: 'sections', let: { classroomId: { $toString: '$_id' } }, pipeline: [{ $match: { $expr: { $eq: ['$classRoomId', '$$classroomId'] } } }], as: 'sections' } }",
            "{ $unwind: '$sections' }",
            "{ $lookup: { from: 'lessons', let: { sectionId: { $toString: '$sections._id' } }, pipeline: [{ $match: { $expr: { $eq: ['$sectionId', '$$sectionId'] } } }], as: 'lessons' } }",
            "{ $unwind: '$lessons' }",
            "{ $lookup: { from: 'deadlines', let: { lessonId: { $toString: '$lessons._id' } }, pipeline: [{ $match: { $expr: { $and: [ { $eq: ['$lessonId', '$$lessonId'] }, { $ne: ['$status', 'NOT_PUBLISHED'] } ] } } }], as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $project: { _id: '$deadlines._id', title: '$deadlines.title', description: '$deadlines.description', type: '$deadlines.type', status: '$deadlines.status', startDate : '$deadlines.startDate ',endDate : '$deadlines.endDate', lessonName: '$lessons.name', lessonDescription: '$lessons.description', sectionName: '$sections.name', sectionDescription: '$sections.description', classroomName: '$name', classroomDescription: '$description' ,allowLateSubmission: '$deadlines.allowLateSubmission'} }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    List<ClassroomDeadlineResponse.DeadlineResponse> getDeadlinesForClassroom(String classroomId, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $match: { _id: ObjectId(?0) } }",
            "{ $lookup: { from: 'sections', let: { classroomId: { $toString: '$_id' } }, pipeline: [{ $match: { $expr: { $eq: ['$classRoomId', '$$classroomId'] } } }], as: 'sections' } }",
            "{ $unwind: '$sections' }",
            "{ $lookup: { from: 'lessons', let: { sectionId: { $toString: '$sections._id' } }, pipeline: [{ $match: { $expr: { $eq: ['$sectionId', '$$sectionId'] } } }], as: 'lessons' } }",
            "{ $unwind: '$lessons' }",
            "{ $lookup: { from: 'deadlines', let: { lessonId: { $toString: '$lessons._id' } }, pipeline: [{ $match: { $expr: { $and: [ { $eq: ['$lessonId', '$$lessonId'] }, { $ne: ['$status', 'NOT_PUBLISHED'] } ] } } }], as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $count: 'total' }"
    })
    long countDeadlinesForClassroom(String classroomId);

    @Aggregation(pipeline = {
            "{ $match: { _id: ObjectId(?0) } }",
            "{ $lookup: { from: 'sections', let: { classroomId: { $toString: '$_id' } }, pipeline: [{ $match: { $expr: { $eq: ['$classRoomId', '$$classroomId'] } } }], as: 'sections' } }",
            "{ $unwind: '$sections' }",
            "{ $lookup: { from: 'lessons', let: { sectionId: { $toString: '$sections._id' } }, pipeline: [{ $match: { $expr: { $eq: ['$sectionId', '$$sectionId'] } } }], as: 'lessons' } }",
            "{ $unwind: '$lessons' }",
            "{ $lookup: { from: 'deadlines', let: { lessonId: { $toString: '$lessons._id' } }, pipeline: [{ $match: { $expr: { $eq: ['$lessonId', '$$lessonId'] } } }], as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $project: { _id: '$deadlines._id', title: '$deadlines.title', description: '$deadlines.description', type: '$deadlines.type', status: '$deadlines.status', startDate : '$deadlines.startDate ',endDate : '$deadlines.endDate', lessonName: '$lessons.name', lessonDescription: '$lessons.description', sectionName: '$sections.name', sectionDescription: '$sections.description', classroomName: '$name', classroomDescription: '$description' ,allowLateSubmission :'$deadlines.allowLateSubmission'} }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    List<ClassroomDeadlineResponse.DeadlineResponse> getDeadlinesForClassroomForTeacher(String classroomId, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $match: { _id: ObjectId(?0) } }",
            "{ $lookup: { from: 'sections', let: { classroomId: { $toString: '$_id' } }, pipeline: [{ $match: { $expr: { $eq: ['$classRoomId', '$$classroomId'] } } }], as: 'sections' } }",
            "{ $unwind: '$sections' }",
            "{ $lookup: { from: 'lessons', let: { sectionId: { $toString: '$sections._id' } }, pipeline: [{ $match: { $expr: { $eq: ['$sectionId', '$$sectionId'] } } }], as: 'lessons' } }",
            "{ $unwind: '$lessons' }",
            "{ $lookup: { from: 'deadlines', let: { lessonId: { $toString: '$lessons._id' } }, pipeline: [{ $match: { $expr: { $eq: ['$lessonId', '$$lessonId'] } } }], as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $count: 'total' }"
    })
    long countDeadlinesForClassroomForTeacher(String classroomId);

    @Query("{ 'inviteCode' : ?0 }")
    ClassRoomEntity findClassRoomEntityByInviteCode(String inviteCode);

    @Query("{ 'teacherId' : ?0 }")
    Page<ClassRoomEntity> findByTeacherId(String teacherId, Pageable pageable);
}