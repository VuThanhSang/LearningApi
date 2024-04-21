package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<TokenEntity, String> {
    @Query("{ 'user' : ?0 }")
    List<TokenEntity> findAllTokenByUser(String userId);
    Optional<TokenEntity> findByToken(String token);
}
