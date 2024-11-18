package com.example.learning_api.entity.sql.database;

import com.example.learning_api.dto.common.FileDto;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "deadline_submissions")
@CompoundIndex(def = "{'title': 'text', 'submission': 'text'}")
public class DeadlineSubmissionsEntity {
    @Id
    private String id;
    private String title;
    private String deadlineId;
    private String studentId;
    private String submission;
    private String grade;
    private String feedback;
    private Boolean isLate;
    private DeadlineSubmissionStatus status;
    private String createdAt;
    private String updatedAt;
}
