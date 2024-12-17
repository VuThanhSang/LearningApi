package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "deadlines")
public class DeadlineEntity {
    @Id
    private String id;
    private String lessonId;
    private String classroomId;
    private String teacherId;
    private String title;
    private String description;
    private DeadlineType type;
    private DeadlineStatus status;
    private Boolean useScoringCriteria;
    private String createdAt;
    private String updatedAt;
    @DBRef
    private List<FileEntity> files;

    @DBRef
    private List<DeadlineSubmissionsEntity> submissions;

}
