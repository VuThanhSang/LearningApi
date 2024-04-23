package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ClassRoomRepository extends MongoRepository<ClassRoomEntity, String>{
    @Query("{ 'name' : { $regex: ?0, $options: 'i' } }")
    Page<ClassRoomEntity> findByNameContaining(String search,Pageable pageable);
}
