package com.example.learning_api.entity.sql.database;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quartz_jobs")
@Data
public class QuartzJobEntity {
    private String jobName;
    private String jobGroup;
    private String jobClass;
    private String cronExpression;
    private String description;
    private String status;
    // Add other fields as needed
}