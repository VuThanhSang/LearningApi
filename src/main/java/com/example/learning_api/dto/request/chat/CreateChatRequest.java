package com.example.learning_api.dto.request.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateChatRequest {
    @NotBlank
    private String meetingId;
    @NotBlank
    private String senderId;
    @NotBlank
    private String message;
    @NotBlank
    private String timestamp;
    @NotBlank
    private String role;
}
