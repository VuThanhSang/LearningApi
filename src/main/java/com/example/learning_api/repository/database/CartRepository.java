package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartRepository extends MongoRepository<CartEntity, String>{

    List<CartEntity> findByUserId(String userId);
}
