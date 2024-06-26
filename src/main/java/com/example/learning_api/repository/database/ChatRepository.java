package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ChatEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<ChatEntity, String> {
}
