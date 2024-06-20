package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.StudentEnrollmentStatus;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "student_enrollments")
public class StudentEnrollmentsEntity {
    @Id
    private String id;
    private String studentId;
    private String classroomId;
    private StudentEnrollmentStatus status;
    private String grade;
    private Date enrolledAt;
    private Date createdAt;
    private Date updatedAt;

}
