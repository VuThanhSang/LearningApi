package com.example.learning_api.dto.response.media;


import com.example.learning_api.entity.sql.database.MediaEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetMediaResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<MediaResponse> media;

    @Data
    public static class FileResponse {
        private String url;
        private String fileType;
        private String fileName;
        private String fileSize;

    }

    @Data
    public static class MediaResponse {
        private String id;
        private String lessonId;
        private String filePath;
        private String description;
        private FileResponse file;
        private String name;
        private String createdAt;
        private String updatedAt;
        public static MediaResponse fromMediaEntity(MediaEntity mediaEntity){
            MediaResponse mediaResponse = new MediaResponse();
            mediaResponse.setId(mediaEntity.getId());
            mediaResponse.setLessonId(mediaEntity.getLessonId());
            mediaResponse.setName(mediaEntity.getName());
            mediaResponse.setFilePath(mediaEntity.getFilePath());
            mediaResponse.setDescription(mediaEntity.getDescription());
            mediaResponse.setCreatedAt(mediaEntity.getCreatedAt());
            mediaResponse.setUpdatedAt(mediaEntity.getUpdatedAt());
            FileResponse fileResponse = new FileResponse();
            fileResponse.setUrl(mediaEntity.getFilePath());

            mediaResponse.setFile(fileResponse);
            return mediaResponse;
        }
    }
}
