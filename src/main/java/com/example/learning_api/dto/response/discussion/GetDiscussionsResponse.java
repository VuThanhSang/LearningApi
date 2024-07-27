package com.example.learning_api.dto.response.discussion;

import com.example.learning_api.entity.sql.database.DiscussionEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetDiscussionsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<DiscussionResponse> discussions;
    @Data
    public static class DiscussionResponse {
        private String id;
        private String title;
        private String content;
        private String authorId;
        private int upvote;
        private int downvote;
        private int commentCount;
        private List<DiscussionEntity.SourceDto> sources;
        private String status;
        private List<String> tags;
        private String createdAt;
        private String updatedAt;
        public static DiscussionResponse formDiscussionEntity(DiscussionEntity discussionEntity){
            DiscussionResponse discussionResponse = new DiscussionResponse();
            discussionResponse.setId(discussionEntity.getId());
            discussionResponse.setTitle(discussionEntity.getTitle());
            discussionResponse.setContent(discussionEntity.getContent());
            discussionResponse.setCreatedAt(discussionEntity.getCreatedAt().toString());
            discussionResponse.setUpdatedAt(discussionEntity.getUpdatedAt().toString());
            discussionResponse.setAuthorId(discussionEntity.getAuthorId());
            discussionResponse.setUpvote(discussionEntity.getUpvote());
            discussionResponse.setDownvote(discussionEntity.getDownvote());
            discussionResponse.setCommentCount(discussionEntity.getCommentCount());
            discussionResponse.setStatus(discussionEntity.getStatus().name());
            return discussionResponse;
        }
    }
}
