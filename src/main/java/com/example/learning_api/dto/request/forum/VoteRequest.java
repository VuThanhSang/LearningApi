package com.example.learning_api.dto.request.forum;

import com.example.learning_api.enums.RoleEnum;
import lombok.Data;

@Data
public class VoteRequest {
    private String authorId;
    private int isUpvote;
    private String forumId;
    private RoleEnum role;

}
