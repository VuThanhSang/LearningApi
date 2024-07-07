package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "summary")
public class SummaryEntity {
    @Id
    private String id;
    private String studentId;
    private String academicYearId;
    private String termId;
    private String courseId;
    private int finalGrade;
    private int midTermGrade;
    private int finalExamGrade;
    private boolean isPassed;
    private Date createdAt;
    private Date updatedAt;
}
