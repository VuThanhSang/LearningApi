package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ApprovalClassroomRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ApprovalClassroomRepository extends MongoRepository<ApprovalClassroomRequestEntity, String> {
    ApprovalClassroomRequestEntity findByClassroomIdAndTeacherId(String classroomId, String teacherId);

    @Query("{ 'classroomId': ?0 }")
    Page<ApprovalClassroomRequestEntity> findAllAnd(String classroomId, Pageable pageable);

    @Query("{ 'classroomId': { $in: ?0 }, 'status': ?1 }")
    Page<ApprovalClassroomRequestEntity> findByClassroomIdsAndStatus(List<String> classroomIds, String status, Pageable pageable);
    @Query("{ 'classroomId': { $in: ?0 } }")
    Page<ApprovalClassroomRequestEntity> findByClassroomIds(List<String> classroomIds, Pageable pageable);
}
