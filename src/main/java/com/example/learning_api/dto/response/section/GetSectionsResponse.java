package com.example.learning_api.dto.response.section;

import com.example.learning_api.entity.sql.database.SectionEntity;
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
}
