package com.example.learning_api.entity.sql.database;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quartz_triggers")
@Data
public class QuartzTriggerEntity {
    private String triggerName;
    private String triggerGroup;
    private String cronExpression;
    private String status;
    private String jobName;
    private String jobGroup;
    // Add other fields as needed
}