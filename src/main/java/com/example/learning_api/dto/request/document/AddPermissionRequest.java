package com.example.learning_api.dto.request.document;

import lombok.Data;

import java.util.List;
@Data
public class AddPermissionRequest {
    private String documentId;
    private String grantedBy;
    private List<Permission> permissions;
    @Data
    public static class Permission {
        private String userId;
        private String accessLevel;
    }
}
