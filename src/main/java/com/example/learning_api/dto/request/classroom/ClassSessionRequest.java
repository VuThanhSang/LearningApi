package com.example.learning_api.dto.request.classroom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClassSessionRequest {
    @Schema(example = "Monday")
    @NotBlank
    private String dayOfWeek;
    @Schema(example = "08:00")
    @NotBlank
    private String startTime;
    @Schema(example = "10:00")
    @NotBlank
    private String endTime;
}