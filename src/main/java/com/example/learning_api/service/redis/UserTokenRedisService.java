//package com.example.learning_api.service.redis;
//
//import com.example.learning_api.constant.ErrorConstant;
//import com.example.learning_api.entity.redis.UserTokenEntity;
//import com.example.learning_api.model.CustomException;
//import com.example.learning_api.repository.redis.UserTokenRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserTokenRedisService {
//    private final UserTokenRepository userTokenRepository;
//
//    public void createNewUserRefreshToken(String refreshToken, String userId) {
//        UserTokenEntity data = UserTokenEntity.builder()
//                .refreshToken(refreshToken)
//                .isUsed(false)
//                .userId(userId)
//                .build();
//        userTokenRepository.save(data);
//    }
//    public void deleteByUserIdAndRefreshToken(String userId, String refreshToken) {
//        UserTokenEntity entity = userTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)
//                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_TOKEN_NOT_FOUND + userId));
//        userTokenRepository.delete(entity);
//    }
//
//    public UserTokenEntity getInfoOfRefreshToken(String refreshToken, String userId) {
//        UserTokenEntity entity = userTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)
//                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_TOKEN_NOT_FOUND + userId));
//        return entity;
//    }
//
//    public void updateUsedUserRefreshToken(UserTokenEntity oldValue) {
//        oldValue.setUsed(true);
//        userTokenRepository.save(oldValue);
//    }
//    public void deleteAllTokenByUserId(String userId) {
//        List<UserTokenEntity> userTokenEntityList = userTokenRepository.findByUserId(userId);
//        for(UserTokenEntity userTokenEntity : userTokenEntityList) {
//            userTokenRepository.delete(userTokenEntity);
//        }
//    }
//}
