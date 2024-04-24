package com.example.learning_api.dto.response.auth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {
    @JsonIgnore
    private String refreshToken;
    private String accessToken;
}
