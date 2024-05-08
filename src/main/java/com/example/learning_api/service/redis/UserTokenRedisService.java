package com.example.learning_api.service.redis;

import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.entity.redis.UserTokenRedisEntity;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.service.redis.Impl.BaseRedisServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTokenRedisService extends BaseRedisServiceImpl {
//    private final UserTokenRepository userTokenRepository;
    private final ObjectMapper objectMapper;
    public UserTokenRedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate);
        this.objectMapper = objectMapper;
    }

    public void createNewUserRefreshToken(String refreshToken, String userId,String email) {
        UserTokenRedisEntity userTokenEntity = UserTokenRedisEntity.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .isUsed(false)
                .build();
        try {
            String userTokenJson = objectMapper.writeValueAsString(userTokenEntity);
            this.hashSet(email, "refresh_token", userTokenJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new CustomException(e.getMessage());

        }
    }
//    public UserTokenRedisEntity findByUserId(String userId) {
//        return userTokenRepository.findByUserId(userId).get(0);
//    }
//    public void deleteByUserIdAndRefreshToken(String userId, String refreshToken) {
//        UserTokenRedisEntity entity = userTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)
//                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_TOKEN_NOT_FOUND + userId));
//        userTokenRepository.delete(entity);
//    }
//
//    public UserTokenRedisEntity getInfoOfRefreshToken(String refreshToken, String userId) {
//        UserTokenRedisEntity entity = userTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)
//                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_TOKEN_NOT_FOUND + userId));
//        return entity;
//    }
//
//    public void updateUsedUserRefreshToken(UserTokenRedisEntity oldValue) {
//        oldValue.setUsed(true);
//        userTokenRepository.save(oldValue);
//    }
//    public void deleteAllTokenByUserId(String userId) {
//        List<UserTokenRedisEntity> userTokenEntityList = userTokenRepository.findByUserId(userId);
//        for(UserTokenRedisEntity userTokenEntity : userTokenEntityList) {
//            userTokenRepository.delete(userTokenEntity);
//        }
//    }
}
