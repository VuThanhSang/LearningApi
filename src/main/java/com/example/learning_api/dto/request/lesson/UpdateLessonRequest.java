package com.example.learning_api.dto.request.lesson;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLessonRequest {
    @NotBlank
    private String id;
    private String name;
    private String description;
    private String status;
    private Integer index;
}
