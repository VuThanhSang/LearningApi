package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "meetings")
public class MeetingEntity {
    @Id
    private String id;
    private String hostId;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private String meetingLink;
    private boolean isRecurring;
    private String status; // active, completed, cancelled
    private Date createdAt;
    private Date updatedAt;
}