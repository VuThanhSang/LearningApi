package com.example.learning_api.dto.response.forum;

import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.ForumEntity;
import com.example.learning_api.entity.sql.database.TagEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetForumsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<ForumResponse> forums;
    @Data
    public static class ForumResponse {
        private String id;
        private String title;
        private String content;
        private String authorId;
        private UserEntity author;
        private int upvote;
        private int downvote;
        private int commentCount;
        private List<FileEntity> sources;
        private String status;
        private List<TagEntity> tags;
        private String createdAt;
        private String updatedAt;
        private Boolean isUpvoted;
        public static ForumResponse formForumEntity(ForumEntity forumEntity){
            ForumResponse forumResponse = new ForumResponse();
            forumResponse.setId(forumEntity.getId());
            forumResponse.setTitle(forumEntity.getTitle());
            forumResponse.setContent(forumEntity.getContent());
            forumResponse.setCreatedAt(forumEntity.getCreatedAt().toString());
            forumResponse.setUpdatedAt(forumEntity.getUpdatedAt().toString());
            forumResponse.setAuthorId(forumEntity.getAuthorId());

            forumResponse.setCommentCount(forumEntity.getCommentCount());
            forumResponse.setStatus(forumEntity.getStatus().name());
            return forumResponse;
        }
    }
}
