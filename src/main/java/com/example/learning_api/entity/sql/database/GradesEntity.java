package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "grades")
public class GradesEntity {
    @Id
    private String id;
    private String studentId;
    private String majorId;
    private String termId;
    private String courseId;
    private int finalGrade;
    private int midTermGrade;
    private int finalExamGrade;
    private boolean isPassed;
}
