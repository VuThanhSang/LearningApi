package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.DiscussionStatus;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.RoleEnum;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "discussions")
public class DiscussionEntity {
    @Id
    private String id;
    private String title;
    private String content;
    private String authorId;
    private List<SourceDto> sources;
    private List<String> tags;
    private DiscussionStatus status;
    private int upvote;
    private int downvote;
    private int commentCount;
    private RoleEnum role;
    private String parentId;
    private String createdAt;
    private String updatedAt;
    @Data
    public static class SourceDto {
        private FaqSourceType type;
        private String path;
    }
}
