package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.deadline.DeadlineStatistics;
import com.example.learning_api.entity.sql.database.DeadlineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DeadlineRepository extends MongoRepository<DeadlineEntity, String> {
    @Query("{'lessonId': ?0}")
    Page<DeadlineEntity> findAllByLessonId(String lessonId, Pageable pageable);
    @Query("{'lessonId': ?0, 'status': {$ne: 'NOT_PUBLISHED'}}")
    Page<DeadlineEntity> findAllByLessonIdForStudent(String lessonId,Pageable pageable);

    @Query("{'teacherId': ?0, " +
            "$and: [" +
            "   {'title': {$regex: ?1, $options: 'i'}}," +
            "   {$or: [" +
            "       {$expr: {$eq: [?2, null]}}, " +
            "       {'status': ?2}" +
            "   ]}" +
            "]}")
    Page<DeadlineEntity> findByTeacherIdWithFiltersForTeacher(String teacherId,
                                                              String search,
                                                              String status,
                                                              Pageable pageable);


    @Aggregation(pipeline = {
            "{ $match: { classroomId: ?0, status: { $ne: 'NOT_PUBLISHED' } } }",
            "{ $lookup: { from: 'student_enrollments', localField: 'classroomId', foreignField: 'classroomId', as: 'enrollments' } }",
            "{ $unwind: '$enrollments' }",
            "{ $lookup: { from: 'deadline_submissions', let: { deadlineId: { $toString: '$_id' }, studentId: '$enrollments.studentId' }, pipeline: [ { $match: { $expr: { $and: [ { $eq: [{ $toString: '$deadlineId' }, '$$deadlineId'] }, { $eq: ['$studentId', '$$studentId'] } ] } } } ], as: 'submissions' } }",
            "{ $addFields: { bestSubmission: { $reduce: { input: '$submissions', initialValue: { grade: '0', status: 'NOT_SUBMITTED', deadlineSubmissionId: null }, in: { $cond: [ { $gt: [{ $toDouble: '$$this.grade' }, { $toDouble: '$$value.grade' }] }, '$$this', '$$value' ] } } } } }",
            "{ $addFields: { grade: { $ifNull: ['$bestSubmission.grade', '0'] }, status: { $ifNull: ['$bestSubmission.status', 'NOT_SUBMITTED'] }, deadlineSubmissionId: '$bestSubmission.deadlineSubmissionId' } }",
            "{ $group: { _id: '$_id', title: { $first: '$title' }, description: { $first: '$description' }, startDate: { $first: '$startDate' }, endDate: { $first: '$endDate' }, students: { $push: { studentId: '$enrollments.studentId', grade: '$grade', status: '$status', deadlineSubmissionId: '$bestSubmission.id' } } } }",
            "{ $sort: { '_id': 1 } }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    List<DeadlineStatistics> getDeadlineStatisticsByClassroomId(String classroomId, long skip, int limit);

    @Aggregation(pipeline = {
            "{ $match: { classroomId: ?0, status: { $ne: 'NOT_PUBLISHED' } } }",
            "{ $count: 'total' }"
    })
    long countDeadlinesByClassroomId(String classroomId);

    @Query("{'classroomId': ?0, 'status': {$ne: 'NOT_PUBLISHED'}}")
    List<DeadlineEntity> findAllByClassroomId(String classroomId);

    @Query("{'teacherId': ?0}")
    List<DeadlineEntity> findAllByTeacherId(String teacherId);

    @Aggregation(pipeline = {
            "{ $match: { classroomId: ?0 } }",
            "{ $count: 'total' }"
    })
    Long countDeadlinesForClassroomForTeacher(String classroomId);

    @Aggregation(pipeline = {
            "{ $match: { classroomId: ?0, status: { $ne: 'NOT_PUBLISHED' } } }",
            "{ $count: 'total' }"
    })
    Long countDeadlinesForClassroom(String classroomId);

    void deleteByLessonId(String lessonId);
}