package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.MediaProgressEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MediaProgressRepository  extends MongoRepository<MediaProgressEntity, String> {
    MediaProgressEntity findByUserIdAndMediaId(String userId, String mediaId);
}
