package com.example.learning_api.dto.response.deadline;

import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.ScoringCriteriaEntity;
import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import com.example.learning_api.repository.database.FileRepository;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
@Builder
public class GetDeadlinesResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<DeadlineResponse> deadlines;
    @Data
    @Builder
    public static class DeadlineResponse {
        private String id;
        private String lessonId;
        private String title;
        private String description;
        private DeadlineType type;
        private DeadlineStatus status;
        private List<FileEntity> files;
        private Boolean useScoringCriteria;
        private List<ScoringCriteriaEntity> scoringCriteria;
        private String createdAt;
        private String updatedAt;
        private String classroomId;
        public static DeadlineResponse fromDeadlineEntity(DeadlineEntity deadlineEntity){
            return DeadlineResponse.builder()
                    .id(deadlineEntity.getId())
                    .lessonId(deadlineEntity.getLessonId())
                    .title(deadlineEntity.getTitle())
                    .description(deadlineEntity.getDescription())
                    .type(deadlineEntity.getType())
                    .status(deadlineEntity.getStatus())
                    .createdAt(deadlineEntity.getCreatedAt())
                    .updatedAt(deadlineEntity.getUpdatedAt())
                    .classroomId(deadlineEntity.getClassroomId())
                    .build();

        }
    }
}
