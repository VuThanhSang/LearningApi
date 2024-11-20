package com.example.learning_api.dto.response.media;

import lombok.Data;

import java.util.List;

@Data
public class GetMediaNotesResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<MediaNoteResponse> mediaNotes;

    @Data
    public static class MediaNoteResponse {
        private String id;
        private String userId;
        private String mediaId;
        private String content;
        private String userName;
        private String userAvatar;
        private String createdAt;
        private String updatedAt;
    }
}
