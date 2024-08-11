package com.example.learning_api.dto.response.deadline;

import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetDeadlinesResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<DeadlineResponse> deadlines;
    @Data
    public static class DeadlineResponse {
        private String id;
        private String lessonId;
        private String title;
        private String description;
        private DeadlineType type;
        private DeadlineStatus status;
        private String attachment;
        private String StartDate;
        private String EndDate;
        private String createdAt;
        private String updatedAt;
        public static DeadlineResponse fromDeadlineEntity(DeadlineEntity deadlineEntity){
            DeadlineResponse deadlineResponse = new DeadlineResponse();
            deadlineResponse.setId(deadlineEntity.getId());
            deadlineResponse.setLessonId(deadlineEntity.getLessonId());
            deadlineResponse.setTitle(deadlineEntity.getTitle());
            deadlineResponse.setDescription(deadlineEntity.getDescription());
            deadlineResponse.setType(deadlineEntity.getType());
            deadlineResponse.setStatus(deadlineEntity.getStatus());
            deadlineResponse.setAttachment(deadlineEntity.getAttachment());
            deadlineResponse.setStartDate(deadlineEntity.getStartDate());
            deadlineResponse.setEndDate(deadlineEntity.getEndDate());
            deadlineResponse.setCreatedAt(deadlineEntity.getCreatedAt());
            deadlineResponse.setUpdatedAt(deadlineEntity.getUpdatedAt());
            return deadlineResponse;

        }
    }
}
