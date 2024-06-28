package com.example.learning_api.dto.request.meeting;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMeetingRequest {
    @NotBlank
    private String id;
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private String meetingLink;
    private boolean isRecurring;
    private String status;
}
