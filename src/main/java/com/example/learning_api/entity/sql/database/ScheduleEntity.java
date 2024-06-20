package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "schedules")
public class ScheduleEntity {
    @Id
    private String id;
    private String startTime;
    private String endTime;
    private String dayOfWeek;
    private String classroomId;
    private Date createdAt;
    private Date updatedAt;

}
