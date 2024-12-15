package com.example.learning_api.dto.response.section;

import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.entity.sql.database.*;
import lombok.Data;

import java.util.List;

@Data
public class GetSectionsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<SectionResponse> sections;
    @Data
    public static class SectionResponse {
        private String id;
        private String name;
        private String description;
        private String classRoomId;
        private String status;
        private int index;
        private List<LessonResponse> lessons;
        private boolean canAccess;
        private boolean isComplete;
        private String createdAt;
        private String updatedAt;
        public static SectionResponse formSectionEntity(SectionEntity sectionEntity){
            SectionResponse sectionResponse = new SectionResponse();
            sectionResponse.setId(sectionEntity.getId());
            sectionResponse.setName(sectionEntity.getName());
            sectionResponse.setDescription(sectionEntity.getDescription());
            sectionResponse.setClassRoomId(sectionEntity.getClassRoomId());
            sectionResponse.setCreatedAt(sectionEntity.getCreatedAt().toString());
            sectionResponse.setUpdatedAt(sectionEntity.getUpdatedAt().toString());
            sectionResponse.setStatus(sectionEntity.getStatus().toString());
            sectionResponse.setIndex(sectionEntity.getIndex());
            return sectionResponse;
        }
    }

    @Data
    public static class LessonResponse{
        private String id;
        private String sectionId;
        private String name;
        private String description;
        private int index;
        private String status;
        private Boolean isComplete;
        private String type;
        private Boolean canAccess;
        private String createdAt;
        private String updatedAt;
    }
}
