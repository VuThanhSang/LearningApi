package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.DeadlineSubmissionStatus;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "deadline_submissions")
public class DeadlineSubmissionsEntity {
    @Id
    private String id;
    private String title;
    private String deadlineId;
    private String studentId;
    private String attachment;
    private String submission;
    private String grade;
    private String feedback;
    private DeadlineSubmissionStatus status;
    private Date createdAt;
    private Date updatedAt;
}
