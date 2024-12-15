package com.example.learning_api.dto.request.progress;

import lombok.Data;

@Data
public class ProgressCompleteRequest {
    private String lessonId;
    private String sectionId;
    private String classroomId;
    private String studentId;
}
