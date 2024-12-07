package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.PermissionDocumentType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "document_permissions")
public class DocumentPermissionEntity {
    @Id
    private String id;

    // User ID của người được cấp quyền
    private String userId;

    // ID của document mà user được phép truy cập
    private String documentId;

    // Mức độ quyền truy cập
    private PermissionDocumentType accessLevel;

    // Thời gian cấp quyền
    private String grantedAt;

    // Thời gian hết hạn quyền (nếu có)
    private String expiresAt;

    // Người cấp quyền
    private String grantedBy;

    // Trạng thái của permission (active, revoked)
    private String status;
}
