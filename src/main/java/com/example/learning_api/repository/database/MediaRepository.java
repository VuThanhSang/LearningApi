package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.MediaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaRepository extends MongoRepository<MediaEntity, String>{
}
