package com.example.learning_api.dto.response.deadline;

import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.ScoringCriteriaEntity;
import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class DeadlineResponse {
    private String id;
    private String lessonId;
    private String classroomId;
    private String teacherId;
    private String title;
    private String description;
    private DeadlineType type;
    private DeadlineStatus status;
    private List<FileEntity> files;
    private Boolean useScoringCriteria;
    private List<ScoringCriteriaEntity> scoringCriteria;

    private String createdAt;
    private String updatedAt;
}
