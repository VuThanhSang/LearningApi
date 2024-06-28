package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "participants")
public class ParticipantEntity {
    @Id
    private String id;
    private String meetingId;
    private String userId;
    private Date joinTime;
    private Date leaveTime;
    private String role; // host, attendee
    private Date createdAt;
    private Date updatedAt;
}