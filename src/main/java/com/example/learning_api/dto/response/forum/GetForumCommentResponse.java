package com.example.learning_api.dto.response.forum;

import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetForumCommentResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<ForumCommentResponse> comments;
    @Data
    public static class ForumCommentResponse {
        private String id;
        private String forumId;
        private String content;
        private String authorId;
        private UserEntity author;
        private List<FileEntity> sources;
        private String parentId;
        private int upvote;
        private int downvote;
        private int replyCount;
        private String status;
        private String createdAt;
        private String updatedAt;
        private Boolean isUpvoted;
    }
}
