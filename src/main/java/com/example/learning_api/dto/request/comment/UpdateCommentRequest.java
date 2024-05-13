package com.example.learning_api.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentRequest {
    @NotBlank
    private String id;
    private String content;
}
