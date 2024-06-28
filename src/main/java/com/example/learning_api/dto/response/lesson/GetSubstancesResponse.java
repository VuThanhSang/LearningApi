package com.example.learning_api.dto.response.lesson;

import com.example.learning_api.entity.sql.database.SubstanceEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetSubstancesResponse{
    private Integer totalPage;
    private Long totalElements;
    private List<SubstanceResponse> substances;
    @Data
    public static class SubstanceResponse {
        private String id;
        private String lessonId;
        private String name;
        private String content;
        private String createdAt;
        private String updatedAt;
        public static SubstanceResponse fromSubstanceEntity(SubstanceEntity substanceEntity){
            SubstanceResponse substanceResponse = new SubstanceResponse();
            substanceResponse.setId(substanceEntity.getId());
            substanceResponse.setLessonId(substanceEntity.getLessonId());
            substanceResponse.setName(substanceEntity.getName());
            substanceResponse.setContent(substanceEntity.getContent());
            substanceResponse.setCreatedAt(substanceEntity.getCreatedAt().toString());
            substanceResponse.setUpdatedAt(substanceEntity.getUpdatedAt().toString());
            return substanceResponse;
        }
    }
}
