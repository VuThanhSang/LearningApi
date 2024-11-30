package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ConfirmationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationRepository extends MongoRepository<ConfirmationEntity, String>{
    Optional<ConfirmationEntity> findByEmail(String s);
    @Query("{email: ?0, code: ?1 }")
    Optional<ConfirmationEntity> findByEmailAndCode(String email, String code);
}
