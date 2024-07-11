package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.DiscussionStatus;
import com.example.learning_api.enums.RoleEnum;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "discussions")
public class DiscussionEntity {
    @Id
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String image;
    private DiscussionStatus status;
    private int upvote;
    private int downvote;
    private int commentCount;
    private RoleEnum role;
    private String createdAt;
    private String updatedAt;
}
