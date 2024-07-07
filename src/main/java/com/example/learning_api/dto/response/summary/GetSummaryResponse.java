package com.example.learning_api.dto.response.summary;

import lombok.Data;

@Data
public class GetSummaryResponse {
    private String id;
    private String termId;
    private String courseId;
    private String courseName;
    private int courseCredit;
    private int finalGrade;
    private int midTermGrade;
    private int finalExamGrade;
    private boolean isPassed;
    private String createdAt;
    private String updatedAt;

}
