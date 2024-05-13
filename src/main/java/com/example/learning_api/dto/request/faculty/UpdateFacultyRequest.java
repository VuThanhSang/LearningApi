package com.example.learning_api.dto.request.faculty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UpdateFacultyRequest {
    @NotBlank
    private String id;
    private String name;
    private String description;
}
