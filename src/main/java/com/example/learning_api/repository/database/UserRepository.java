package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
    @Query("{ 'email' : ?0 }")
    Optional<UserEntity> findByEmail(String email);
    @Query("{ 'email' : ?0, 'authType' : ?1 }")
    Optional<UserEntity> findByEmailAndAuthType(String email, String authType);
    int countAllByRole(String role);
    long countByRole(String role);

    long countByStatus(String status);
    @Query("{ 'fullname': { $regex: ?0, $options: 'i' } }")
    List<UserEntity> findIdsByFullnameRegex(String regex);
}
