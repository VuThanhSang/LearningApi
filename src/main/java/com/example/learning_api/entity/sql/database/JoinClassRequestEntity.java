package com.example.learning_api.entity.sql.database;


import com.example.learning_api.enums.JoinRequestStatus;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "join_class_requests")
public class JoinClassRequestEntity {
    @Id
    private String id;
    private String studentId;
    private String classroomId;
    private JoinRequestStatus status; // Pending, Approved, Rejected
    private String createdAt;
    private String updatedAt;
}
