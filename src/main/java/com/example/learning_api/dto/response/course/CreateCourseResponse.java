package com.example.learning_api.dto.response.course;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCourseResponse {
    private String id;
    private String name;
    private String description;
    private String teacherId;
}
