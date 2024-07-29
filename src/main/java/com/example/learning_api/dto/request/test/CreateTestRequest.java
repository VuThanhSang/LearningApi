package com.example.learning_api.dto.request.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Builder
public class CreateTestRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private String classroomId;
    @NotBlank
    private String teacherId;
    @NotBlank
    private int duration;
    @NotBlank
    private String startTime;
    @NotBlank
    private String endTime;
    @NotBlank
    private MultipartFile source;
    @NotBlank
    private String showResultType;
    @NotBlank
    private String startAt;
    @NotBlank
    private String endAt;
    @NotBlank
    private String Status;
    @NotBlank
    private Integer attemptLimit;
}
