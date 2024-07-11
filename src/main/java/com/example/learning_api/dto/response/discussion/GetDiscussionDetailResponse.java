package com.example.learning_api.dto.response.discussion;

import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import com.example.learning_api.enums.DiscussionStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;

import java.util.List;

@Data
public class GetDiscussionDetailResponse {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String image;
    private DiscussionStatus status;
    private int upvote;
    private int downvote;
    private int commentCount;
    private RoleEnum role;
    private String createdAt;
    private String updatedAt;
    private List<DiscussionComment> comments;
    @Data
    public static class DiscussionComment {
        private String id;
        private String discussionId;
        private String content;
        private int upvote;
        private int downvote;
        private int replyCount;
        private DiscussionStatus status;
        private String image;
        private RoleEnum role;
        private String createdAt;
        private String updatedAt;
        private StudentEntity student;
        private TeacherEntity teacher;
    }
}
