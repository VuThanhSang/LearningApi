package com.example.learning_api.dto.response.deadline;


import com.example.learning_api.entity.sql.database.FileEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UpcomingDeadlinesResponse {
    private String id;
    private String title;
    private String description;
    private String type;
    private String status;
    private List<FileEntity> files;
    private String classroomId;
    private Boolean allowLateSubmission;
    private String startDate;
    private String endDate;
    private String lessonName;

    // Getters and setters...
}