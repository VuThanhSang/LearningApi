package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "media_progress")
public class MediaProgressEntity {
    @Id
    private String id;
    private String userId;
    private String mediaId;
    private Integer watchedDuration; // Thời lượng đã xem (giây)
    private boolean completed;
    private String lastWatchedAt;
}
