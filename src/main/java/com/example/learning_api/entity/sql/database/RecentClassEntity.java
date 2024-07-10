package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "recent_class")
public class RecentClassEntity {
    @Id
    private String id;
    private String studentId;
    private String classroomId;
    private String teacherId;
    private Date lastAccessedAt;
}
