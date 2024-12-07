package com.example.learning_api.dto.response.document;

import com.example.learning_api.entity.sql.database.BlockEntity;
import com.example.learning_api.enums.ForumStatus;
import lombok.Data;

import java.util.List;

@Data
public class GetDocumentDetailResponse {
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private Boolean isNeedPermission;
    private ForumStatus status;
    private String createdAt;
    private String updatedAt;
    private List<BlockEntity> blocks;
}
