package com.example.learning_api.dto.response.AuthResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterResponse {
    private String userId;
    private String accessToken;
    private String refreshToken;
}

