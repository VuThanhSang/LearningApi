package com.example.learning_api.entity.redis;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTokenRedisEntity {
    private String userId;
    private String refreshToken;
    private boolean isUsed;
}
