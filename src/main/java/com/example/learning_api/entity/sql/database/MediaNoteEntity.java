package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.RoleEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "media_note")
public class MediaNoteEntity {
    private String id;
    private String mediaId;
    private String userId;
    private String content;
    private RoleEnum role;
    private Integer time; // Thời điểm trong video (giây)
    private String createdAt;
}
