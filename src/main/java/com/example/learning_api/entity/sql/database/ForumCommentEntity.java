package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.ForumStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "forum_comment")
public class ForumCommentEntity {
    private String id;
    private String forumId;
    private String content;
    private String authorId;
    private int replyCount;
    private ForumStatus status;
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
