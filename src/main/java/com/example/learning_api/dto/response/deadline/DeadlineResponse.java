package com.example.learning_api.dto.response.deadline;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeadlineResponse {
    private String id;
    private String title;
    private String description;
    private String type;
    private String status;
    private String startDate;
    private String endDate;
    private String classroomId;
}
