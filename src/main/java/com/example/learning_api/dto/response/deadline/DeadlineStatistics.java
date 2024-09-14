package com.example.learning_api.dto.response.deadline;


import lombok.Data;

import java.util.List;

@Data
public class DeadlineStatistics {
    private String _id; // This will be mapped from _id in the aggregation result
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private List<StudentSubmission> students;

    @Data
    public static class StudentSubmission {
        private String studentId;
        private String studentName;
        private String grade;
        private String status;
    }
}