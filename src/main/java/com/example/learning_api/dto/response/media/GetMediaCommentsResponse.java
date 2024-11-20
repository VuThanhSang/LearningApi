package com.example.learning_api.dto.response.media;

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
        private String mediaId;
        private String content;
        private String userName;
        private String userAvatar;
        private String createdAt;
        private String updatedAt;
        private Boolean isReply;
        private String replyTo;
        private Integer totalReply;
    }
}
