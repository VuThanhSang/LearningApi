package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "recordings")
public class RecordingEntity {
    @Id
    private String id;
    private String meetingId;
    private String fileUrl;
    private int duration;
    private Date createdAt;
}