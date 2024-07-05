package com.example.learning_api.dto.request.faculty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CreateFacultyRequest {
    @NotBlank
    private String name;
    private String description;
    private String status;
    private String dean;



}
