package com.example.learning_api.dto.response.classroom;

import lombok.Data;

import java.util.Date;

@Data
public class RecentClassDTO {
    private String lastAccessedAt;
    private String facultyId;
    private int enrollmentCapacity;
    private int currentEnrollment;
    private String status;
    private int credits;
    private String termId;
    private String classId;
    private String className;
    private String description;
    private String image;
    private String courseId;

    // Constructors, getters, and setters
}