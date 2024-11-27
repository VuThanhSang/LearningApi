package com.example.learning_api.dto.response.media;

import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetMediaCommentsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<MediaCommentResponse> mediaComments;

    @Data
    public static class MediaCommentResponse {
        private String id;
        private String userId;
        private UserEntity user;
        private String mediaId;
        private String content;
        private String createdAt;
        private String updatedAt;
        private Boolean isReply;
        private String replyTo;
        private Integer totalReply;
    }
}
