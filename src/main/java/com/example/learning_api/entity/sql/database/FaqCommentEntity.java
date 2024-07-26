package com.example.learning_api.entity.sql.database;


import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FaqStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "faq_comment")
public class FaqCommentEntity {
    private String id;
    private String faqId;
    private String userId;
    private String content;
    private String parentId;
    private FaqStatus status;
    private RoleEnum role;
    private List<SourceDto> sources;
    private String createdAt;
    private String updatedAt;
    @Data
    public static class SourceDto {
        private FaqSourceType type;
        private String path;
    }
}
