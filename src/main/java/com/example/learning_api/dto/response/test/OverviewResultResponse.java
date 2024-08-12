package com.example.learning_api.dto.response.test;

import lombok.Data;

import java.util.List;

@Data
public class OverviewResultResponse {
    private List<TestResultOfTestResponse> testResults;
    private int totalStudent;
    private int totalPassed;
    private int totalFailed;
    private int totalNotAttended;
    private int totalGrade;
    private double averageGrade;
    private int maxGrade;
    private int minGrade;

}
