package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "media_comment")
public class MediaCommentEntity {
    @Id
    private String id;
    private String mediaId;
    private String userId;
    private String content;
    private String createdAt;
    private Boolean isReply;
    private String replyTo;

}
