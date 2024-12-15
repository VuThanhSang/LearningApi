package com.example.learning_api.repository.database;

import com.example.learning_api.dto.common.TotalTestOfDayDto;
import com.example.learning_api.entity.sql.database.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public interface TestRepository extends MongoRepository<TestEntity, String> {
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'status': {$in: ['UPCOMING', 'ONGOING', 'FINISHED']}}")
    Page<TestEntity> findByNameContaining(String name, Pageable pageable);
    @Query("{'classroomId': ?0, 'status': {$in: ['UPCOMING', 'ONGOING', 'FINISHED']}}")
    Page<TestEntity> findByClassroomId(String classroomId, Pageable pageable);

    List<TestEntity> findByClassroomId(String classroomId);

    @Query("{'classroomId': ?0}")
    Page<TestEntity> findByClassroomIdAndStatus(String classroomId, Pageable pageable);

    @Query("{'classroomId': ?0}")
    List<TestEntity> findAllByClassroomId(String classroomId);


    List<TestEntity> findByLessonId(String lessonId);

    void deleteByLessonId(String classroomId);
}
