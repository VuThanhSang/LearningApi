package com.example.learning_api.dto.request.forum;

import com.example.learning_api.enums.RoleEnum;
import lombok.Data;

@Data
public class VoteRequest {
    private String authorId;
    private Boolean isUpvote;
    private String targetId;
    private String targetType;
    private RoleEnum role;

}
