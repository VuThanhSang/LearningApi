package com.example.learning_api.dto.request.lesson;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateLessonRequest {
    @NotBlank
    private String sectionId;
    @NotBlank
    private String name;
    private String type;
    private String description;
    private String status;

}
