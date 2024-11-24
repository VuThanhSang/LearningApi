package com.example.learning_api.dto.response.forum;

import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.enums.ForumStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;

import java.util.List;

@Data
public class GetForumDetailResponse {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String image;
    private ForumStatus status;
    private int commentCount;
    private RoleEnum role;
    private int upvoteCount;
    private int downvoteCount;
    private String createdAt;
    private String updatedAt;
    private UserEntity author;
    private List<FileEntity> sources;
    private List<ForumComment> comments;
    private Boolean isUpvoted;
    @Data
    public static class ForumComment {
        private String id;
        private String forumId;
        private String content;
        private String authorId;
        private UserEntity author;
        private int upvote;
        private int downvote;
        private int replyCount;
        private ForumStatus status;
        private List<FileEntity> sources;
        private RoleEnum role;
        private String createdAt;
        private String updatedAt;
        private Boolean isUpvoted;

        private StudentEntity student;
        private TeacherEntity teacher;
    }
}
