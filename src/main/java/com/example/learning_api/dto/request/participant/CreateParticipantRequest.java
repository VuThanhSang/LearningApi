package com.example.learning_api.dto.request.participant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateParticipantRequest {
    @NotBlank
    private String meetingId;
    @NotBlank
    private String userId;
    private String joinTime;
    private String leaveTime;
    @NotBlank
    private String role;

}
