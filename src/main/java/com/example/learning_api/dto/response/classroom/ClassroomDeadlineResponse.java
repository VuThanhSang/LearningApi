package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.ScoringCriteriaEntity;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ClassroomDeadlineResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<DeadlineResponse> deadlines;
    @Data
    @Builder
    public static class DeadlineResponse {
        private String id;
        private String title;
        private String description;
        private String type;
        private String status;
        private String startDate;
        private String endDate;
        private List<FileEntity> files;
        private List<ScoringCriteriaEntity> scoringCriteria;
        private Boolean useScoringCriteria;
        private String lessonName;
        private String lessonDescription;
        private String sectionName;
        private String sectionDescription;
        private String classroomName;
        private String classroomDescription;
    }

}
