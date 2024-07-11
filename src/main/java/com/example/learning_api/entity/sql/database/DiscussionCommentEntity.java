package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.DiscussionStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "discussion_comments")
public class DiscussionCommentEntity {
    private String id;
    private String discussionId;
    private String content;
    private String authorId;
    private int upvote;
    private int downvote;
    private int replyCount;
    private DiscussionStatus status;
    private String image;
    private RoleEnum role;
    private String createdAt;
    private String updatedAt;
}
