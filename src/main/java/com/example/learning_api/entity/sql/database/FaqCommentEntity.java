package com.example.learning_api.entity.sql.database;


import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FaqStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
    private String createdAt;
    private String updatedAt;
    @DBRef
    private List<FileEntity> sources;
    @Data
    public static class SourceDto {
        private FaqSourceType type;
        private String path;
    }
}
