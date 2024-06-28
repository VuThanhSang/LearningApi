package com.example.learning_api.dto.request.meeting;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMeetingRequest {
    @NotBlank
    private String teacherId;
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private String startTime;
    @NotBlank
    private String endTime;
    private String meetingLink;
    private boolean isRecurring;
    private String status;



}
