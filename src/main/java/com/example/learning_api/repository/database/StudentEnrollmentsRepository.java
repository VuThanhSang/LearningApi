package com.example.learning_api.repository.database;

import com.example.learning_api.dto.common.StudentSubmissionCountDto;
import com.example.learning_api.dto.response.classroom.GetClassRoomRecentResponse;
import com.example.learning_api.dto.response.classroom.GetScheduleResponse;
import com.example.learning_api.dto.response.deadline.DeadlineResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlinesResponse;
import com.example.learning_api.dto.response.deadline.UpcomingDeadlinesResponse;
import com.example.learning_api.dto.response.test.TestResultForStudentResponse;
import com.example.learning_api.dto.response.test.TestResultsForClassroomResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.enums.StudentEnrollmentStatus;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface StudentEnrollmentsRepository extends MongoRepository<StudentEnrollmentsEntity, String> {
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
    @Aggregation(pipeline = {
            "{$addFields: {_classroomId: {$toObjectId: '$classroomId'}}}",
            "{$match: {studentId: '?0'}}",
            "{$lookup: {from: 'classrooms', localField: '_classroomId', foreignField: '_id', as: 'classroom'}}",
            "{$unwind: '$classroom'}",
            "{$lookup: {from: 'recent_class', let: {classroomId: '$classroomId', studentId: '$studentId'}, pipeline: [{$match: {$expr: {$and: [{$eq: ['$classroomId', '$$classroomId']}, {$eq: ['$studentId', '$$studentId']}]}}}], as: 'recentAccess'}}",
            "{$unwind: {path: '$recentAccess', preserveNullAndEmptyArrays: true}}",
            "{$project: {_id: '$classroom._id', name: '$classroom.name',image:'$classroom.image', description: '$classroom.description', courseId: '$classroom.courseId', termId: '$classroom.termId', teacherId: '$classroom.teacherId', createdAt: '$classroom.createdAt', updatedAt: '$classroom.updatedAt', enrolledAt: '$enrolledAt', lastAccessedAt: {$ifNull: ['$recentAccess.lastAccessedAt', null]}}}",
            "{$sort: {lastAccessedAt: -1}}"
    })
    Slice<GetClassRoomRecentResponse.ClassRoomResponse> getRecentClasses(String studentId, Pageable pageable);
    @Aggregation(pipeline = {
            "{ $match: { studentId: ?0 } }",
            "{ $lookup: { from: 'deadlines', localField: 'classroomId', foreignField: 'classroomId', as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $match: { $expr: { $and: [ { $eq: ['$deadlines.status', 'UPCOMING'] }, { $gt: ['$deadlines.endDate',  ?1 ] }, { $lt: ['$deadlines.endDate',  ?2 ] } ] } } }",
            "{ $project: { " +
                    "_id: '$deadlines._id', " +
                    "title: '$deadlines.title', " +
                    "description: '$deadlines.description', " +
                    "type: '$deadlines.type', " +
                    "status: '$deadlines.status', " +
                    "startDate: '$deadlines.startDate', " +
                    "endDate: '$deadlines.endDate', " +
                    "allowLateSubmission: '$deadlines.allowLateSubmission', " +
                    "} }"
    })
    List<UpcomingDeadlinesResponse> getUpcomingDeadlines(String studentId, String startDate, String endDate, Pageable pageable);
    @Aggregation(pipeline = {
            "{ $match: { studentId: ?0 } }",
            "{ $lookup: { from: 'deadlines', localField: 'classroomId', foreignField: 'classroomId', as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $match: { $expr: { $and: [ { $eq: ['$deadlines.status', 'UPCOMING'] }, { $gt: ['$deadlines.endDate',  ?1 ] }, { $lt: ['$deadlines.endDate',  ?2 ] } ] } } }",
            "{ $count: 'total' }"
    })
    Long countUpcomingDeadlines(String studentId, String startDate, String endDate);
    @Query("{'classroomId': ?0}")
    Page<StudentEnrollmentsEntity> findByClassroomId(String classroomId, Pageable pageable);
    List<StudentEnrollmentsEntity> findByClassroomId(String classroomId);
    @Aggregation(pipeline = {
            "{$match: {classroomId: '?0'}}",
            "{$lookup: {from: 'test', localField: 'classroomId', foreignField: 'classroomId', as: 'tests'}}",
            "{$unwind: '$tests'}",
            "{$lookup: {from: 'test_results', let: { testId: {$toString:'$tests._id'}, studentId: '$studentId' }, pipeline: [{$match: {$expr: {$and: [{ $eq: ['$testId', '$$testId'] },{ $eq: ['$studentId', '$$studentId'] }]}}}], as: 'result'}}",
            "{$unwind: {path: '$result', preserveNullAndEmptyArrays: true}}",
            "{$project: {_id: 0, studentId: 1, testId: '$tests._id', testName: '$tests.name', grade: { $ifNull: ['$result.grade', null] }, isPassed: { $ifNull: ['$result.isPassed', null] }, state: { $ifNull: ['$result.state', 'NOT_ATTENDED'] }, attendedAt: { $ifNull: ['$result.attendedAt', null] }, finishedAt: { $ifNull: ['$result.finishedAt', null] }}}",
            "{$group: {_id: {testId: '$testId', studentId: '$studentId'}, testName: { $first: '$testName' }, result: {$push: {grade: '$grade', isPassed: '$isPassed', state: '$state', attendedAt: '$attendedAt', finishedAt: '$finishedAt'}}}}",
            "{$group: {_id: '$_id.testId', testName: { $first: '$testName' }, students: {$push: {studentId: '$_id.studentId', result: '$result'}}}}",
            "{$project: {_id: 0, testId: '$_id', testName: 1, students: 1}}"
    })
    List<TestResultsForClassroomResponse> getTestResultsForClassroom(String classroomId);
    @Aggregation(pipeline = {
            "{$match: {classroomId: ?1, studentId: ?0}}",
            "{$lookup: {from: 'test', localField: 'classroomId', foreignField: 'classroomId', as: 'tests'}}",
            "{$unwind: '$tests'}",
            "{$lookup: {from: 'test_results', let: { testId: {$toString:'$tests._id'}, studentId: '$studentId' }, pipeline: [{$match: {$expr: {$and: [{ $eq: ['$testId', '$$testId'] },{ $eq: ['$studentId', '$$studentId'] }]}}}], as: 'result'}}",
            "{$unwind: {path: '$result', preserveNullAndEmptyArrays: true}}",
            "{$project: {_id: 0, testId: '$tests._id', testName: '$tests.name', grade: { $ifNull: ['$result.grade', null] }, isPassed: { $ifNull: ['$result.isPassed', null] }, state: { $ifNull: ['$result.state', 'NOT_ATTENDED'] }, attendedAt: { $ifNull: ['$result.attendedAt', null] }, finishedAt: { $ifNull: ['$result.finishedAt', null] }}}",
            "{$group: {_id: '$testId', testName: { $first: '$testName' }, results: {$push: {grade: '$grade', isPassed: '$isPassed', state: '$state', attendedAt: '$attendedAt', finishedAt: '$finishedAt'}}}}"
    })
    List<TestResultForStudentResponse> getTestResultsForStudent(String studentId, String classroomId);
    int countByClassroomId(String classroomId);
    @Aggregation(pipeline = {
            "{$match: {classroomId: ?0}}",
            "{$lookup: {from: 'test_results', let: { student_id: '$studentId' }, pipeline: [{$match: {$expr: {$and: [{ $eq: ['$studentId', '$$student_id'] },{ $eq: ['$testId', ?1] }]}}}], as: 'result'}}",
            "{$match: {result: { $size: 0 }}}",
            "{$project: {_id: 0, studentId: 1}}"
    })
    List<String> findStudentsNotTakenTest(String classroomId, String testId);
    @Aggregation(pipeline = {
            "{$match: {classroomId: ?0}}",
            "{$lookup: {from: 'test_results', let: { student_id: '$studentId' }, pipeline: [{$match: {$expr: {$and: [{ $eq: ['$studentId', '$$student_id'] },{ $eq: ['$testId', ?1] }]}}}], as: 'result'}}",
            "{$match: {$expr: {$gt: [{$size: '$result'}, 0]}}}",
            "{$group: {_id: '$studentId'}}",
            "{$project: {_id: 0, studentId: '$_id'}}"
    })
    List<String> findStudentsTakenTest(String classroomId, String testId);
    @Aggregation(pipeline = {
            "{$match: {classroomId: ?0}}",
            "{$lookup: {from: 'deadlines', localField: 'classroomId', foreignField: 'classroomId', as: 'deadlines'}}",
            "{$unwind: '$deadlines'}",
            "{$lookup: {from: 'deadline_submissions', let: { studentId: '$studentId', deadlineId: {$toString: '$deadlines._id'} }, pipeline: [{$match: {$expr: {$and: [{ $eq: ['$studentId', '$$studentId'] },{ $eq: ['$deadlineId', '$$deadlineId'] }]}}}] , as: 'submissions'}}",
            "{$match: {$expr: {$eq: [{$size: '$submissions'}, 0]}}}",
            "{$project: {_id: 0, studentId: 1}}"
    })
    List<String> findStudentsNotTakenDeadline(String classroomId, String deadlineId);
    @Aggregation(pipeline = {
            "{ $match: { studentId: ?0 } }",
            "{ $lookup: { from: 'deadlines', localField: 'classroomId', foreignField: 'classroomId', as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $match: { $and: [ " +
                    "{ $or: [ { 'deadlines.status': ?1 }, { $expr: { $eq: [?1, ''] } } ] }, " +
                    "{ $or: [ " +
                    "{ $and: [ " +
                    "{ $expr: { $ne: [?2, ''] } }, " +
                    "{ 'deadlines.title': { $regex: ?2, $options: 'i' } } " +
                    "] }, " +
                    "{ $expr: { $eq: [?2, ''] } } " +
                    "] }, " +
                    "{ $or: [ { 'deadlines.classroomId': ?3 }, { $expr: { $eq: [?3, ''] } } ] } " +
                    "] } }",
            "{ $project: { " +
                    "_id: '$deadlines._id', " +
                    "title: '$deadlines.title', " +
                    "description: '$deadlines.description', " +
                    "type: '$deadlines.type', " +
                    "status: '$deadlines.status', " +
                    "startDate: '$deadlines.startDate', " +
                    "endDate: '$deadlines.endDate', " +
                    "classroomId: '$deadlines.classroomId', " +
                    "allowLateSubmission: '$deadlines.allowLateSubmission' " +
                    "} }",
            "{ $sort: ?4 }"  // Dynamic sort stage
    })
    Slice<GetDeadlinesResponse.DeadlineResponse> getStudentDeadlines(
            String studentId,
            String status,
            String title,
            String classroomId,
            Document sort,  // MongoDB Document representing sort order
            Pageable pageable
    );

    @Aggregation(pipeline = {
            "{ $match: { studentId: ?0 } }",
            "{ $lookup: { from: 'deadlines', localField: 'classroomId', foreignField: 'classroomId', as: 'deadlines' } }",
            "{ $unwind: '$deadlines' }",
            "{ $match: { $and: [ " +
                    "{ $or: [ { 'deadlines.status': ?1 }, { $expr: { $eq: [?1, ''] } } ] }, " +
                    "{ $or: [ " +
                    "{ $and: [ " +
                    "{ $expr: { $ne: [?2, ''] } }, " +
                    "{ 'deadlines.title': { $regex: ?2, $options: 'i' } } " +
                    "] }, " +
                    "{ $expr: { $eq: [?2, ''] } } " +
                    "] }, " +
                    "{ $or: [ { 'deadlines.classroomId': ?3 }, { $expr: { $eq: [?3, ''] } } ] } " +
                    "] } }",
            "{ $count: 'total' }"
    })
    Long countStudentDeadlines(
            String studentId,
            String status,
            String title,
            String classroomId
    );

    StudentEnrollmentsEntity findByStudentIdAndClassroomId(String studentId, String classroomId);
    List<StudentEnrollmentsEntity> findByClassroomIdAndStatus(String classroomId, StudentEnrollmentStatus status);
    @Aggregation(pipeline = {
            "{$addFields: { createdAtLong: { $toLong: '$createdAt' } }}",
            "{$match: { createdAtLong: { $exists: true, $ne: null } }}",
            "{$group: { _id: { $dateToString: { format: '%Y-%m', date: { $toDate: '$createdAtLong' } } }, enrollmentCount: { $sum: 1 } }}",
            "{$sort: { _id: 1 }}"
    })
    List<StudentSubmissionCountDto> getMonthlyEnrollmentStats();

    @Aggregation(pipeline = {
            "{ $group: { _id: '$classroomId', enrollmentCount: { $sum: 1 } } }",
            "{ $sort: { enrollmentCount: -1 } }",
            "{ $limit: 5 }"
    })
    List<StudentSubmissionCountDto> getTopEnrolledClassrooms();
    @Aggregation(pipeline = {
            "{$match: { " +
                    "$expr: { " +
                    "$and: [ " +
                    "{ $eq: [ { $year: '$createdAt' }, { $year: new Date() } ] }, " +
                    "{ $eq: [ { $month: '$createdAt' }, { $month: new Date() } ] } " +
                    "] " +
                    "} " +
                    "}}",
            "{$count: 'currentMonthEnrollments'}"
    })
    Long countCurrentMonthEnrollments();


}