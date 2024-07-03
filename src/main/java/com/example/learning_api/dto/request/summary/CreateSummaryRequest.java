package com.example.learning_api.dto.request.summary;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSummaryRequest {
    @NotNull
    private String studentId;
    @NotNull
    private String majorId;
    @NotNull
    private String termId;
    @NotNull
    private String courseId;
    @NotNull
    private int finalGrade;
    @NotNull
    private int midTermGrade;
    @NotNull
    private int finalExamGrade;
    @NotNull
    private boolean isPassed;
}
