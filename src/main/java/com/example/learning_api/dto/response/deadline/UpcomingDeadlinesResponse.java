package com.example.learning_api.dto.response.deadline;


import lombok.Data;

import java.util.Date;

@Data
public class UpcomingDeadlinesResponse {
    private String id;
    private String title;
    private String description;
    private String type;
    private String status;
    private String attachment;
    private String startDate;
    private String endDate;
    private String lessonName;
    private String lessonDescription;
    private String sectionName;
    private String sectionDescription;

    // Getters and setters...
}