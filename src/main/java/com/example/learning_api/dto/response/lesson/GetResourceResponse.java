package com.example.learning_api.dto.response.lesson;

import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.entity.sql.database.ResourceEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetResourceResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<ResourceResponse> resources;

    @Data
    public static class FileResponse {
        private String url;
        private String fileType;
        private String fileName;
        private String fileSize;

    }
    @Data
    public static class ResourceResponse {
        private String id;
        private String lessonId;
        private String filePath;
        private FileResponse file;
        private String description;
        private String name;
        private Date createdAt;
        private Date updatedAt;
        public static ResourceResponse fromResourceEntity(ResourceEntity resourceEntity){
            ResourceResponse resourceResponse = new ResourceResponse();
            resourceResponse.setId(resourceEntity.getId());
            resourceResponse.setLessonId(resourceEntity.getLessonId());
            resourceResponse.setName(resourceEntity.getName());
            resourceResponse.setFilePath(resourceEntity.getFilePath());
            resourceResponse.setDescription(resourceEntity.getDescription());
            resourceResponse.setCreatedAt(resourceEntity.getCreatedAt());
            resourceResponse.setUpdatedAt(resourceEntity.getUpdatedAt());
            FileResponse fileResponse = new FileResponse();
            fileResponse.setUrl(resourceEntity.getFilePath());
            fileResponse.setFileType(resourceEntity.getFileType());
            fileResponse.setFileName(resourceEntity.getFileName());
            fileResponse.setFileSize(resourceEntity.getFileSize());
            resourceResponse.setFile(fileResponse);
            return resourceResponse;
        }
    }
}
