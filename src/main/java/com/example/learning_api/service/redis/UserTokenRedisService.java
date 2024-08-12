//package com.example.learning_api.service.redis;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//import com.example.learning_api.constant.ProjectConstant;
//
//@Service
//@RequiredArgsConstructor
//public class UserTokenRedisService {
//    private final RedisTemplate<String, Object> redisTemplate;
//    public static final String PREFIX_KEY_USER_TOKEN = "user_token";
//    public final static String STRING_FORMAT_KEY_USER_TOKEN = PREFIX_KEY_USER_TOKEN + ":%s:%s";
//    private static final int MAX_TOKENS_PER_USER = 1;
//    public static final String PREFIX_KEY_USER_STATUS = "user_status";
//    public final static String STRING_FORMAT_KEY_USER_STATUS = PREFIX_KEY_USER_STATUS + ":%s";
//
//    private String getKeyUserStatus(String userId) {
//        return String.format(STRING_FORMAT_KEY_USER_STATUS, userId);
//    }
//
//    public void setUserOnline(String userId) {
//        String key = getKeyUserStatus(userId);
//        redisTemplate.opsForValue().set(key, "online");
//    }
//
//    public void setUserOffline(String userId) {
//        String key = getKeyUserStatus(userId);
//        redisTemplate.opsForValue().set(key, "offline");
//    }
//
//    public String getUserStatus(String userId) {
//        String key = getKeyUserStatus(userId);
//        return (String) redisTemplate.opsForValue().get(key);
//    }
//
//
//    private String getKeyUserTokenKey(String userId, String token) {
//        return String.format(STRING_FORMAT_KEY_USER_TOKEN, userId, token);
//    }
//    public void upsertUserToken(String userId, String token, boolean isUsed) {
//        String key = getKeyUserTokenKey(userId, token);
//        Set<String> userTokens = getAllTokensForUser(userId);
//
//        if (userTokens.size() >= MAX_TOKENS_PER_USER) {
//            // If the limit is reached, remove the oldest token
//            String oldestToken = userTokens.iterator().next();
//            redisTemplate.delete(getKeyUserTokenKey(userId, oldestToken));
//        }
//        redisTemplate.opsForValue().set(key, isUsed, ProjectConstant.REFRESH_TOKEN_EXPIRE_MINUTES_TIME, TimeUnit.MINUTES);
//    }
//    public Boolean getUserTokenValue(String userId, String token) {
//        String key = getKeyUserTokenKey(userId, token);
//        return (Boolean) redisTemplate.opsForValue().get(key);
//    }
//    public Set<String> getAllTokensForUser(String userId) {
//        String pattern = getKeyUserTokenKey(userId, "*");
//        return redisTemplate.keys(pattern);
//    }
//    public void deleteAllTokenOfUser(String userId) {
//        String pattern = getKeyUserTokenKey(userId, "*");
//        Set<String> keys = redisTemplate.keys(pattern);
//        if (keys != null) {
//            redisTemplate.delete(keys);
//        }
//    }
//}