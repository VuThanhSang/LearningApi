package com.example.learning_api.dto.common;

import lombok.Data;

@Data
public class LessonCompleteDto {
    private String lessonId;
    private String lessonName;
    private String createdAt;
}
