package com.example.learning_api.dto.request.document;


import com.example.learning_api.enums.ForumStatus;
import lombok.Data;

@Data
public class UpdateDocumentRequest {
    private String id;
    private String name;
    private String description;
    private ForumStatus status;
    private Boolean isNeedPermission;

}
