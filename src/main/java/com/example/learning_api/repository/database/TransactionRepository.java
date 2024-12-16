package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TransactionRepository extends MongoRepository<TransactionEntity, Long> {
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
}
