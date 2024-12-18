package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.ApprovalClassStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "approval_classroom_requests")
public class ApprovalClassroomRequestEntity {
    private String id;
    private String classroomId;
    private String teacherId;
    private ApprovalClassStatus status;
    private String createdAt;
    private String updatedAt;
}
