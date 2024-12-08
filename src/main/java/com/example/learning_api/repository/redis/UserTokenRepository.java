//package com.example.learning_api.repository.redis;
//import com.example.learning_api.entity.redis.UserTokenEntity;
//import org.springframework.data.repository.CrudRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface UserTokenRepository extends CrudRepository<UserTokenEntity, String> {
//    Optional<UserTokenEntity> findByUserIdAndRefreshToken(String userId, String refreshToken);
//    List<UserTokenEntity> findByUserId(String userId);
//}