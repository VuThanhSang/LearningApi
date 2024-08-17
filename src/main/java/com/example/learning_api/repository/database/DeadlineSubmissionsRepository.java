package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DeadlineSubmissionsRepository extends MongoRepository<DeadlineSubmissionsEntity, String> {
    Page<DeadlineSubmissionsEntity> findAllByDeadlineId(String deadlineId, org.springframework.data.domain.Pageable pageable);
    Page<DeadlineSubmissionsEntity> findAllByStudentIdAndDeadlineId(String studentId,String deadlineId , org.springframework.data.domain.Pageable pageable);
    @Query("{'deadlineId': ?0, $or: [{'title': {$regex: ?1, $options: 'i'}}, {'submission': {$regex: ?1, $options: 'i'}}]}")
    Page<DeadlineSubmissionsEntity> findAllByDeadlineIdAndSearch(String deadlineId, String search, Pageable pageable);

    @Query("{'deadlineId': ?0, 'status': ?1}")
    Page<DeadlineSubmissionsEntity> findAllByDeadlineIdAndStatus(String deadlineId, String status, Pageable pageable);

    @Query("{'deadlineId': ?0, $or: [{'title': {$regex: ?1, $options: 'i'}}, {'submission': {$regex: ?1, $options: 'i'}}], 'status': ?2}")
    Page<DeadlineSubmissionsEntity> findAllByDeadlineIdAndSearchAndStatus(String deadlineId, String search, String status, Pageable pageable);
    @Query("{'deadlineId': ?0, " +
            "$and: [" +
            "  {$or: [" +
            "    {$and: [" +
            "      {$expr: {$ne: [?1, null]}}, " +
            "      {$or: [" +
            "        {'title': {$regex: ?1, $options: 'i'}}, " +
            "        {'submission': {$regex: ?1, $options: 'i'}}" +
            "      ]}" +
            "    ]}, " +
            "    {$expr: {$eq: [?1, null]}}" +
            "  ]}, " +
            "  {$or: [" +
            "    {$and: [" +
            "      {$expr: {$ne: [?2, null]}}, " +
            "      {'status': ?2}" +
            "    ]}, " +
            "    {$expr: {$eq: [?2, null]}}" +
            "  ]}" +
            "]}")
    Page<DeadlineSubmissionsEntity> findAllByDeadlineIdWithFilters(
            String deadlineId, String search, String status, Pageable pageable);
}
