package com.example.learning_api.dto.request.discussion;

import com.example.learning_api.enums.RoleEnum;
import lombok.Data;

@Data
public class VoteRequest {
    private String authorId;
    private int isUpvote;
    private String discussionId;
    private RoleEnum role;

}
