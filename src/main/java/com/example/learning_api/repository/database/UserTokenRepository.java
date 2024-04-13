package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.UserTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserTokenRepository extends MongoRepository<UserTokenEntity, String> {
}
