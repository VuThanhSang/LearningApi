package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.classroom.ClassroomDeadlineResponse;
import com.example.learning_api.dto.response.classroom.TotalClassroomOfDayDto;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ClassRoomRepository extends MongoRepository<ClassRoomEntity, String> {
    @Query("{ 'name' : { $regex: ?0, $options: 'i' }, 'status': { $ne: 'BLOCKED' } }")
    Page<ClassRoomEntity> findByNameContaining(String search, Pageable pageable);

    @Query("{ 'name' : { $regex: ?0, $options: 'i' } }")
    Page<ClassRoomEntity> findByNameContainingForAdmin(String search, Pageable pageable);

    @Query("{ 'name' : { $regex: ?0, $options: 'i' }, 'status' : ?1 }")
    Page<ClassRoomEntity> findByNameContainingAndStatus(String search, String status, Pageable pageable);

    @Query("{ 'name' : { $regex: ?0, $options: 'i' }, 'status' : ?1 }")
    Page<ClassRoomEntity> findByNameContainingAndStatusForAdmin(String search, String status, Pageable pageable);

    @Query("{ '_id' : { $in: ?0 }, 'name' : { $regex: ?1, $options: 'i' }, 'status': { $ne: 'BLOCKED' } }")
    Page<ClassRoomEntity> findByIdInAndNameContaining(List<String> classroomIds, String search, Pageable pageable);

    @Query("{ '_id' : { $in: ?0 }, 'name' : { $regex: ?1, $options: 'i' }, 'status': ?2 }")
    Page<ClassRoomEntity> findByIdInAndNameContainingAndStatus(List<String> classroomIds, String search, String status, Pageable pageable);

    @Query("{ 'teacherId' : ?0, 'name' : { $regex: ?1, $options: 'i' }, 'status': { $ne: 'BLOCKED' } }")
    Page<ClassRoomEntity> findByTeacherIdAndNameContaining(String teacherId, String search, Pageable pageable);

    @Query("{ 'teacherId' : ?0, 'name' : { $regex: ?1, $options: 'i' }, 'status': ?2 }")
    Page<ClassRoomEntity> findByTeacherIdAndNameContainingAndStatus(String teacherId, String search, String status, Pageable pageable);

    @Query("{ 'courseId' : ?0, 'status': { $ne: 'BLOCKED' } }")
    Page<ClassRoomEntity> findByCourseId(String courseId, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { 'sessions.dayOfWeek': ?0, 'status': { $ne: 'BLOCKED' } } }",
            "{ $addFields: { daySpecificSessions: { $filter: { input: '$sessions', as: 'session', cond: { $eq: ['$$session.dayOfWeek', ?0] } } } } }",
            "{ $lookup: { from: 'student_enrollments', let: { classroomId: { $toString: '$_id' } }, pipeline: [{ $match: { $expr: { $and: [{ $eq: ['$studentId', ?1] }, { $eq: ['$classroomId', '$$classroomId'] }] } } }], as: 'enrollment' } }",
            "{ $project: { '_id': 1, 'name': 1, 'description': 1, 'image': 1, 'teacherId': 1, 'courseId': 1, 'sessions': '$daySpecificSessions', 'enrollment': { $arrayElemAt: ['$enrollment', 0] } } }"
    })
    List<ClassRoomEntity> findStudentScheduleByDayAndStudentId(String dayOfWeek, String studentId);

    @Aggregation(pipeline = {
            "{ $unwind: '$sessions' }",
            "{ $addFields: { 'dayOfWeek': { $let: { vars: { 'days': [ 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday' ] }, in: { $arrayElemAt: [ '$$days', { $subtract: [ { $dayOfWeek: { $toDate: '$sessions.startTime' } }, 1 ] } ] } } } } }",
            "{ $match: { 'status': { $ne: 'BLOCKED' } } }",
            "{ $group: { _id: '$dayOfWeek', count: { $sum: 1 } } }"
    })
    List<TotalClassroomOfDayDto> countSessionsByDayOfWeek();

    @Aggregation(pipeline = {
            "{ $match: { _id: ObjectId(?0), 'status': { $ne: 'BLOCKED' } } }",
            "{ $lookup: { from: 'sections', let: { classroomId: { $toString: '$_id' } }, pipeline: [{ $match: { $expr: { $eq: ['$classRoomId', '$$classroomId'] } } }], as: 'sections' } }",
            "{ $unwind: '$sections' }",
            "{ $lookup: { from: 'lessons', let: { sectionId: { $toString: '$sections._id' } }, pipeline: [{ $match: { $expr: { $eq: ['$sectionId', '$$sectionId'] } } }], as: 'lessons' } }",
            "{ $unwind: '$lessons' }",
            "{ $lookup: { from: 'deadlines', let: { lessonId: { $toString: '$lessons._id' } }, pipeline: [{ $match: { $expr: { $eq: ['$lessonId', '$$lessonId'] } } }], as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $project: { _id: '$deadlines._id', title: '$deadlines.title', description: '$deadlines.description', type: '$deadlines.type', status: '$deadlines.status', startDate : '$deadlines.startDate ',endDate : '$deadlines.endDate', lessonName: '$lessons.name', lessonDescription: '$lessons.description', sectionName: '$sections.name', sectionDescription: '$sections.description', classroomName: '$name', classroomDescription: '$description' ,allowLateSubmission: '$deadlines.allowLateSubmission'} }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    List<ClassroomDeadlineResponse.DeadlineResponse> getDeadlinesForClassroom(String classroomId, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $match: { _id: ObjectId(?0), 'status': { $ne: 'BLOCKED' } } }",
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

    @Query("{ 'inviteCode' : ?0, 'status': { $ne: 'BLOCKED' } }")
    ClassRoomEntity findClassRoomEntityByInviteCode(String inviteCode);

    @Query("{ 'teacherId' : ?0, 'status': { $ne: 'BLOCKED' } }")
    Page<ClassRoomEntity> findByTeacherId(String teacherId, Pageable pageable);
    List<ClassRoomEntity> findByTeacherId(String teacherId);

    @Query("{ '_id' : { $in: ?0 }, 'categoryId' : ?1, 'name' : { $regex: ?2, $options: 'i' }, 'status': { $ne: 'BLOCKED' } }")
    Page<ClassRoomEntity> findByCategoryAndNameContaining(List<String> classroomIds, String categoryId, String search, Pageable pageable);

    @Query("{ '_id' : { $in: ?0 }, 'categoryId' : ?1, 'name' : { $regex: ?2, $options: 'i' }, 'status': ?3 }")
    Page<ClassRoomEntity> findByCategoryAndNameContainingAndStatus(List<String> classroomIds, String categoryId, String search, String status, Pageable pageable);

    @Query("{ 'teacherId' : ?0, 'categoryId' : ?1, 'name' : { $regex: ?2, $options: 'i' }, 'status': ?3 }")
    Page<ClassRoomEntity> findByTeacherIDCategoryAndNameContainingAndStatus(String teacherId, String categoryId, String search, String status, Pageable pageable);

    @Query("{ 'teacherId' : ?0, 'categoryId' : ?1, 'name' : { $regex: ?2, $options: 'i' }, 'status': 'COMPLETED")
    Page<ClassRoomEntity> findByTeacherIDCategoryAndNameContaining(String teacherId, String categoryId, String search, Pageable pageable);

    @Query("{ '_id': { $nin: ?0 }, 'name': { $regex: ?1, $options: 'i' }, 'status': 'COMPLETED")
    Page<ClassRoomEntity> findByIdNotInAndNameContaining(List<String> excludedIds, String name, Pageable pageable);

    @Query("{ '_id': { $nin: ?0 }, 'name': { $regex: ?1, $options: 'i' }, 'status': ?2 }")
    Page<ClassRoomEntity> findByIdNotInAndNameContainingAndStatus(List<String> excludedIds, String name, String status, Pageable pageable);

    @Query("{ 'categoryId': ?0, 'name': { $regex: ?1, $options: 'i' }, '_id': { $nin: ?2 } }")
    Page<ClassRoomEntity> findByCategoryAndNameContainingAndIdNotIn(String categoryId, String name, List<String> excludedIds, Pageable pageable);

    @Query("{'_id': { $nin: ?0 }, 'categoryId': ?1, 'name': { $regex: ?2, $options: 'i' }, 'status': ?3 }")
    Page<ClassRoomEntity> findByCategoryAndNameContainingAndStatusNotIn(List<String> excludedIds, String categoryId, String name, String status, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { '_id': { $nin: ?0 }, 'status': 'COMPLETED', 'name': { $regex: ?1, $options: 'i' } } }",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $skip: ?#{#pageable.offset} }",
            "{ $limit: ?#{#pageable.pageSize} }"
    })
    List<ClassRoomEntity> findNewClassrooms(List<String> excludedIds, String search, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { '_id': { $nin: ?0 }, 'status': ?2, 'name': { $regex: ?1, $options: 'i' } } }",
            "{ $sort: { 'currentEnrollment': -1 } }"
    })
    List<ClassRoomEntity> findPopularClassrooms(List<String> excludedIds, String search, String status);

    @Aggregation(pipeline = {
            "{ $match: { '_id': { $nin: ?0 }, 'status': 'COMPLETED', 'name': { $regex: ?1, $options: 'i' } } }",
            "{ $sample: { size: ?2 } }"
    })
    List<ClassRoomEntity> findRandomClassrooms(List<String> excludedIds, String search, int sampleSize);

    @Aggregation(pipeline = {
            "{ $match: { '_id': { $nin: ?0 }, 'categoryId': ?1, 'status': ?3, 'name': { $regex: ?2, $options: 'i' } } }",
            "{ $sort: { 'currentEnrollment': -1 } }"
    })
    List<ClassRoomEntity> findPopularClassroomsByCategory(List<String> excludedIds, String categoryId, String search, String status);

    @Aggregation(pipeline = {
            "{ $match: { '_id': { $nin: ?0 }, 'categoryId': ?1, 'status': ?3, 'name': { $regex: ?2, $options: 'i' } } }",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $skip: ?#{#pageable.offset} }",
            "{ $limit: ?#{#pageable.pageSize} }"
    })
    List<ClassRoomEntity> findNewClassroomsByCategory(List<String> excludedIds, String categoryId, String search, Pageable pageable, String status);

    @Aggregation(pipeline = {
            "{ $match: { '_id': { $nin: ?0 }, 'categoryId': ?1, 'status': 'COMPLETED', 'name': { $regex: ?2, $options: 'i' } } }",
            "{ $sample: { size: ?3 } }"
    })
    List<ClassRoomEntity> findRandomClassroomsByCategory(List<String> excludedIds, String categoryId, String search, int sampleSize);

    @Aggregation(pipeline = {
            "{ $match: { category: ?0, name: { $regex: ?1, $options: 'i' }, classroomId: { $nin: ?2 } } }",
            "{ $sort: { price: ?4 } }",
            "{ $skip: ?3 }",
            "{ $limit: ?5 }"
    })
    List<ClassRoomEntity> findByCategoryAndSortByPrice(String category, List<String> registeredClassRoomIds, String search, Pageable pageable, boolean ascending);

    @Aggregation(pipeline = {
            "{ $match: { '_id': { $nin: ?0 }, 'status': 'COMPLETED', 'name': { $regex: ?1, $options: 'i' } } }"
    })
    List<ClassRoomEntity> findByAndSortByPrice(List<String> registeredClassRoomIds, String search, Pageable pageable);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<ClassRoomEntity> findIdsByNameRegex(String regex);
}