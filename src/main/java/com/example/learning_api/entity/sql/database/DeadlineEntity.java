package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "deadlines")
public class DeadlineEntity {
    @Id
    private String id;
    private String lessonId;
    private String title;
    private String description;
    private DeadlineType type;
    private DeadlineStatus status;
    private String attachment;
    private Date dueDate;
    private Date createdAt;
    private Date updatedAt;

}
