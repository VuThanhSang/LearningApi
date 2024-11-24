package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.RoleEnum;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "votes")
public class VoteEntity {
    @Id
    private String id;
    private String authorId;
    private RoleEnum role;
    private boolean isUpvote;
    private String targetId;
    private String targetType;
    @DBRef
    private ForumEntity forum;


}