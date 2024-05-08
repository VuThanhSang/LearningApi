//package com.example.learning_api.repository.redis;
//
//import com.example.learning_api.entity.redis.UserTokenRedisEntity;
//import org.springframework.data.repository.CrudRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface UserTokenRepository extends CrudRepository<UserTokenRedisEntity, String> {
//    Optional<UserTokenRedisEntity> findByUserIdAndRefreshToken(String userId, String refreshToken);
//    List<UserTokenRedisEntity> findByUserId(String userId);
//
//}
