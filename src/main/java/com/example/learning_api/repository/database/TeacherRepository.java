package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TeacherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TeacherRepository extends MongoRepository<TeacherEntity, String>{
  @Query("{'user.fullname': {$regex: ?0, $options: 'i'}}")
  Page<TeacherEntity> findByNameContaining(String name, Pageable pageable);

  TeacherEntity findByUserId(String userId);

  @Query("{'user.fullname': {$regex: ?0, $options: 'i'}, 'user.status': {$regex: ?1, $options: 'i'}}")
  Page<TeacherEntity> findByNameContainingAndStatus(String name, String status, Pageable pageable);

}

