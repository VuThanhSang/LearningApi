//package com.example.learning_api.repository.redis;
//
//import com.example.learning_api.entity.sql.database.UserFCMTokenEntity;
//import org.springframework.data.repository.CrudRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface UserTokenRepository extends CrudRepository<UserFCMTokenEntity, String> {
//    Optional<UserFCMTokenEntity> findByUserIdAndRefreshToken(String userId, String refreshToken);
//    List<UserFCMTokenEntity> findByUserId(String userId);
//
//}
