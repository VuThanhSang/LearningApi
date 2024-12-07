package com.example.learning_api.dto.request.document;

import com.example.learning_api.enums.ForumStatus;
import com.example.learning_api.enums.RoleEnum;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateDocumentRequest {
    private String name;
    private String ownerId;
    private String description;
    private Boolean isNeedPermission;
    private ForumStatus status;

}
