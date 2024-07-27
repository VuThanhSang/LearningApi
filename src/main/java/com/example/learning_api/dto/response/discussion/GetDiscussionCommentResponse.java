package com.example.learning_api.dto.response.discussion;

import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetDiscussionCommentResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<DiscussionCommentResponse> comments;
    @Data
    public static class DiscussionCommentResponse {
        private String id;
        private String discussionId;
        private String content;
        private String authorId;
        private int upvote;
        private int downvote;
        private int replyCount;
        private String status;
        private String createdAt;
        private String updatedAt;
        private StudentEntity student;
        private TeacherEntity teacher;
    }
}
