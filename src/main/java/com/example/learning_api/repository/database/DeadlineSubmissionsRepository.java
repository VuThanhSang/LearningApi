package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DeadlineSubmissionsRepository extends MongoRepository<DeadlineSubmissionsEntity, String> {
    Page<DeadlineSubmissionsEntity> findAllByDeadlineId(String deadlineId, org.springframework.data.domain.Pageable pageable);
    List<DeadlineSubmissionsEntity> findAllByDeadlineIdAndStatus(String deadlineId, DeadlineSubmissionStatus status);
    Page<DeadlineSubmissionsEntity> findAllByStudentIdAndDeadlineId(String studentId,String deadlineId , org.springframework.data.domain.Pageable pageable);
    @Query("{'deadlineId': ?0, $or: [{'title': {$regex: ?1, $options: 'i'}}, {'submission': {$regex: ?1, $options: 'i'}}]}")
    Page<DeadlineSubmissionsEntity> findAllByDeadlineIdAndSearch(String deadlineId, String search, Pageable pageable);

    @Query("{'deadlineId': ?0, 'status': ?1}")
    Page<DeadlineSubmissionsEntity> findAllByDeadlineIdAndStatus(String deadlineId, String status, Pageable pageable);

    @Query("{'deadlineId': ?0, $or: [{'title': {$regex: ?1, $options: 'i'}}, {'submission': {$regex: ?1, $options: 'i'}}], 'status': ?2}")
    Page<DeadlineSubmissionsEntity> findAllByDeadlineIdAndSearchAndStatus(String deadlineId, String search, String status, Pageable pageable);
    @Aggregation(pipeline = {
            "{ $match: { 'deadlineId': ?0 } }",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $group: { " +
                    "_id: '$studentId', " +
                    "doc: { $first: '$$ROOT' } " +
                    "} }",
            "{ $replaceRoot: { newRoot: '$doc' } }",
            "{ $match: { " +
                    "$and: [ " +
                    "{ $or: [ " +
                    "{ $and: [ " +
                    "{ $expr: { $ne: [?1, null] } }, " +
                    "{ $or: [ " +
                    "{ 'title': { $regex: ?1, $options: 'i' } }, " +
                    "{ 'submission': { $regex: ?1, $options: 'i' } } " +
                    "]} " +
                    "]}, " +
                    "{ $expr: { $eq: [?1, null] } } " +
                    "]}, " +
                    "{ $or: [ " +
                    "{ $and: [ " +
                    "{ $expr: { $ne: [?2, null] } }, " +
                    "{ 'status': ?2 } " +
                    "]}, " +
                    "{ $expr: { $eq: [?2, null] } } " +
                    "]} " +
                    "] " +
                    "} }"
    })
    List<DeadlineSubmissionsEntity> findAllByDeadlineIdWithFilters(
            String deadlineId, String search, String status, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { 'deadlineId': ?0 } }",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $group: { " +
                    "_id: '$studentId', " +
                    "doc: { $first: '$$ROOT' } " +
                    "} }",
            "{ $replaceRoot: { newRoot: '$doc' } }",
            "{ $match: { " +
                    "$and: [ " +
                    "{ $or: [ " +
                    "{ $and: [ " +
                    "{ $expr: { $ne: [?1, null] } }, " +
                    "{ $or: [ " +
                    "{ 'title': { $regex: ?1, $options: 'i' } }, " +
                    "{ 'submission': { $regex: ?1, $options: 'i' } } " +
                    "]} " +
                    "]}, " +
                    "{ $expr: { $eq: [?1, null] } } " +
                    "]}, " +
                    "{ $or: [ " +
                    "{ $and: [ " +
                    "{ $expr: { $ne: [?2, null] } }, " +
                    "{ 'status': ?2 } " +
                    "]}, " +
                    "{ $expr: { $eq: [?2, null] } } " +
                    "]} " +
                    "] " +
                    "} }",
            "{ $count: 'total' }"
    })
    long countAllByDeadlineIdWithFilters(String deadlineId, String search, String status);


    @Aggregation(pipeline = {
            "{ $match: { studentId: ?0 } }",
            "{$addFields: {_deadlineId: {$toObjectId: '$deadlineId'}}}",
            "{ $lookup: { " +
                    "from: 'deadlines', " +
                    "localField: '_deadlineId', " +
                    "foreignField: '_id', " +
                    "as: 'deadlineInfo' " +
                    "} }",
            "{ $unwind: '$deadlineInfo' }",
            "{ $match: { 'deadlineInfo.classroomId': ?1 } }",
            "{ $project: { " +
                    "_id: 1, " +
                    "title: 1, " +
                    "deadlineId: 1, " +
                    "studentId: 1, " +
                    "submission: 1, " +
                    "grade: 1, " +
                    "feedback: 1, " +
                    "status: 1, " +
                    "createdAt: 1, " +
                    "updatedAt: 1 " +
                    "} }"
    })
    List<DeadlineSubmissionsEntity> findByStudentIdAndClassroomId(String studentId, String classroomId);

    @Query("{'studentId': ?0, 'deadlineId': ?1}")
    List<DeadlineSubmissionsEntity> findByStudentIdAndDeadlineId(String studentId, String deadlineId);
}
