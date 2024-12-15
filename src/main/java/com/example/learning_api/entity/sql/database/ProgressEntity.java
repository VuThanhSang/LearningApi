package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "progress")
public class ProgressEntity  {
    @Id
    private String id;
    private String studentId; // ID học viên
    private String classroomId; // ID lớp học
    private String sectionId; // ID section (nếu bài học trong section)
    private String lessonId; // ID bài học
    private Boolean completed; // Hoàn thành hay chưa
    private String completedAt; // Thời điểm hoàn thành
}
