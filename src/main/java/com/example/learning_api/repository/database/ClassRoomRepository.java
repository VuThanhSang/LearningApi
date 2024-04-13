package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClassRoomRepository extends MongoRepository<ClassRoomEntity, String>{
}
