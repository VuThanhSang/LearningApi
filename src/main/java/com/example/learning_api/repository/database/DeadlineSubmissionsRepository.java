package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeadlineSubmissionsRepository extends MongoRepository<DeadlineSubmissionsEntity, String> {
    Page<DeadlineSubmissionsEntity> findAllByDeadlineId(String deadlineId, org.springframework.data.domain.Pageable pageable);
    Page<DeadlineSubmissionsEntity> findAllByStudentIdAndDeadlineId(String studentId,String deadlineId , org.springframework.data.domain.Pageable pageable);
}
