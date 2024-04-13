package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.IntensionRoomEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IntensionRoomRepository extends MongoRepository<IntensionRoomEntity, String>{
}
