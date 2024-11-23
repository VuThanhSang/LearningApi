package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.ForumStatus;
import com.example.learning_api.enums.RoleEnum;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "forum")
public class ForumEntity {
    @Id
    private String id;
    private String title;
    private String content;
    private String authorId;
    private List<String> tags;
    private ForumStatus status;
    @Min(0)
    private int viewCount;

    @Min(0)
    private int commentCount;
    @DBRef
    private List<VoteEntity> votes;
    @DBRef
    private List<ForumCommentEntity> comments;
    private RoleEnum role;
    private String parentId;
    private String createdAt;
    private String updatedAt;

}