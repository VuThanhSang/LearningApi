package com.example.learning_api.dto.response.lesson;

import com.example.learning_api.entity.sql.database.*;
import lombok.Data;

import java.util.List;

@Data
public class GetLessonDetailResponse {
    private String id;
    private String sectionId;
    private String name;
    private String description;
    private int index;
    private String status;
    private List<ResourceEntity> resources;
    private List<MediaEntity> media;
    private List<SubstanceEntity> substances;
    private List<DeadlineEntity> deadlines;
    private String createdAt;
    private String updatedAt;

}
