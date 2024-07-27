package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.DiscussionStatus;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
    private List<SourceDto> attachments;
    private RoleEnum role;
    private String parentId;
    private String createdAt;
    private String updatedAt;
    @Data
    public static class SourceDto {
        private FaqSourceType type;
        private String path;
    }
}
