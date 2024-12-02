package com.example.learning_api.dto.response.faq;

import com.example.learning_api.entity.sql.database.FaqCommentEntity;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.enums.FaqStatus;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
public class GetFaqDetailResponse {
    private String id;
    private String question;
    private String userId;
    private FaqStatus status;
    private String createdAt;
    private String updatedAt;
    private List<FileEntity> sources;
    private List<FaqCommentEntity> comments;
}
