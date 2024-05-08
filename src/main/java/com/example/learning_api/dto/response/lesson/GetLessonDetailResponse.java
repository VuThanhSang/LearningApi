package com.example.learning_api.dto.response.lesson;

import com.example.learning_api.entity.sql.database.MediaEntity;
import com.example.learning_api.entity.sql.database.ResourceEntity;
import com.example.learning_api.entity.sql.database.SubstanceEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetLessonDetailResponse {
    private String id;
    private String sectionId;
    private String name;
    private String description;
    private List<ResourceEntity> resources;
    private List<MediaEntity> media;
    private List<SubstanceEntity> substances;
    private String createdAt;
    private String updatedAt;

}
