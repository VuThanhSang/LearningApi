package com.example.learning_api.dto.request.summary;

import lombok.Data;

@Data
public class UpdateSummaryRequest {
    private String id;
    private int finalGrade;
    private int midTermGrade;
    private int finalExamGrade;
    private boolean isPassed;
}
