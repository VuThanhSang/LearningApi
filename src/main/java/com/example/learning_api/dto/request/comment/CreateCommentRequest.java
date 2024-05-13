package com.example.learning_api.dto.request.comment;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank
    private String faqId;
    @NotBlank
    private String userId;
    @NotBlank
    private String content;
    private String parentId;

}
