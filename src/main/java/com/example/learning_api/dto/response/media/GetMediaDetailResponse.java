package com.example.learning_api.dto.response.media;

import lombok.Data;

import java.util.List;

@Data
public class GetMediaDetailResponse {
    private String id;
    private String lessonId;
    private String url;
    private String fileType;
    private String fileName;
    private String fileSize;
    private String thumbnailPath;
    private Integer duration;
    private String classroomId;
    private String description;
    private String name;
    private String createdAt;
    private String updatedAt;
    private List<TimeGroupedNotes> notes;

    @Data
    public static class TimeGroupedNotes {
        private Integer time;
        private List<MediaNote> mediaNotes;
    }

    @Data
    public static class MediaNote {
        private String id;
        private String content;
        private String authorName;
        private String authorId;
        private String avatar;
        private String importanceLevel;
        private String createdAt;
        private String updatedAt;
    }
}