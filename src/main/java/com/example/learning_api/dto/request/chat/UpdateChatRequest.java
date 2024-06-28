package com.example.learning_api.dto.request.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateChatRequest {
    @NotBlank
    private String id;
    private String message;
    @NotBlank
    private String senderId;
    private String timestamp;
}
