package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {
    @Query("{'orderId': ?0, 'userId': ?1}")
    TransactionEntity findByOrderIdAndUserId(String orderId, String userId);

    @Query("{'transactionRef': ?0}")
    List<TransactionEntity> findByTransactionRef(String transactionRef);

    @Query("{'userId': ?0, 'classroomId': ?1, 'status': ?2}")
    TransactionEntity findByUserIdAndClassroomIdAndStatus(String userId, String classroomId, String status);

    List<TransactionEntity> findByClassroomIdIn(List<String> classroomIds);
    Page<TransactionEntity> findByClassroomIdIn(List<String> classroomIds, Pageable pageable);
    Page<TransactionEntity> findByUserId(String userId, Pageable pageable);

    Page<TransactionEntity> findAllByStatus(String status, Pageable pageable);

    Page<TransactionEntity> findByUserIdIn(List<String> userIds, Pageable pageable);


    Page<TransactionEntity> findByStatusAndUserIdIn(String status, List<String> userIds, Pageable pageable);

    Page<TransactionEntity> findByStatusAndClassroomIdIn(String status, List<String> classroomIds, Pageable pageable);

    Page<TransactionEntity> findByUserIdInAndClassroomIdIn(List<String> userIds, List<String> classroomIds, Pageable pageable);
    Page<TransactionEntity> findByUserIdAndClassroomIdIn(String userId, List<String> classroomIds, Pageable pageable);
    Page<TransactionEntity> findByStatusAndUserIdAndClassroomIdIn(String status, String userId, List<String> classroomIds, Pageable pageable);
    Page<TransactionEntity> findByStatusAndUserId(String status, String userId, Pageable pageable);
    Page<TransactionEntity> findByStatusAndUserIdInAndClassroomIdIn(String status, List<String> userIds, List<String> classroomIds, Pageable pageable);


    Page<TransactionEntity> findByUserIdInAndCreatedAtBetween(List<String> userIds, String startTimestamp, String endTimestamp, Pageable pageable);

    Page<TransactionEntity> findByClassroomIdInAndCreatedAtBetween(List<String> classroomIds, String startTimestamp, String endTimestamp, Pageable pageable);

    Page<TransactionEntity> findAllByCreatedAtBetween(String startTimestamp, String endTimestamp, Pageable pageable);

    Page<TransactionEntity> findByStatusAndCreatedAtBetween(String status, String startTimestamp, String endTimestamp, Pageable pageable);

    Page<TransactionEntity> findByStatusAndUserIdInAndCreatedAtBetween(String status, List<String> userIds, String startTimestamp, String endTimestamp, Pageable pageable);

    Page<TransactionEntity> findByStatusAndClassroomIdInAndCreatedAtBetween(String status, List<String> classroomIds, String startTimestamp, String endTimestamp, Pageable pageable);


    @Query("{ 'userId': { $in: ?0 }, 'classroomId': { $in: ?1 }, 'createdAt': { $gte: ?2, $lte: ?3 } }")
    Page<TransactionEntity> findByUserIdInAndClassroomIdInAndCreatedAtBetween(
            List<String> userIds,
            List<String> classroomIds,
            String startTimestamp,
            String endTimestamp,
            Pageable pageable
    );

    // Query to find transactions by status, userId, classroomId, and createdAt range
    @Query("{ 'status': ?0, 'userId': { $in: ?1 }, 'classroomId': { $in: ?2 }, 'createdAt': { $gte: ?3, $lte: ?4 } }")
    Page<TransactionEntity> findByStatusAndUserIdInAndClassroomIdInAndCreatedAtBetween(
            String status,
            List<String> userIds,
            List<String> classroomIds,
            String startTimestamp,
            String endTimestamp,
            Pageable pageable
    );


    @Query("{ 'userId': ?0, 'classroomId': { $in: ?1 }, 'createdAt': { $gte: ?2, $lte: ?3 } }")
    Page<TransactionEntity> findByUserIdAndClassroomIdInAndCreatedAtBetween(
            String userId,
            List<String> classroomIds,
            String startTimestamp,
            String endTimestamp,
            Pageable pageable
    );

    // Find transactions by status, userId, classroomIds, and createdAt range
    @Query("{ 'status': ?0, 'userId': ?1, 'classroomId': { $in: ?2 }, 'createdAt': { $gte: ?3, $lte: ?4 } }")
    Page<TransactionEntity> findByStatusAndUserIdAndClassroomIdInAndCreatedAtBetween(
            String status,
            String userId,
            List<String> classroomIds,
            String startTimestamp,
            String endTimestamp,
            Pageable pageable
    );

    // Find transactions by userId and createdAt range
    @Query("{ 'userId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    Page<TransactionEntity> findByUserIdAndCreatedAtBetween(
            String userId,
            String startTimestamp,
            String endTimestamp,
            Pageable pageable
    );

    // Find transactions by status, userId, and createdAt range
    @Query("{ 'status': ?0, 'userId': ?1, 'createdAt': { $gte: ?2, $lte: ?3 } }")
    Page<TransactionEntity> findByStatusAndUserIdAndCreatedAtBetween(
            String status,
            String userId,
            String startTimestamp,
            String endTimestamp,
            Pageable pageable
    );
}
