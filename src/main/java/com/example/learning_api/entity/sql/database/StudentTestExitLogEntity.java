package com.example.learning_api.entity.sql.database;


import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "student_test_exit_logs")
public class StudentTestExitLogEntity {
    @Id
    private String id;
    private String studentId;
    private String testResultId;
    private String time;
}
