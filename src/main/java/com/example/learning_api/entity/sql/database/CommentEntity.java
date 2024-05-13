package com.example.learning_api.entity.sql.database;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "comments")
public class CommentEntity {
    private String id;
    private String faqId;
    private String userId;
    private String content;
    private String parentId;
    private Date createdAt;
    private Date updatedAt;
}
