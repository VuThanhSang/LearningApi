package com.example.learning_api.dto.request.student_enrollments;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollStudentRequest {
    @NotNull
    private String studentId;
    @NotNull
    private String courseId;
    @NotNull
    private String classroomId;

}
