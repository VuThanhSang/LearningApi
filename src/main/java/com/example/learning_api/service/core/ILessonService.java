package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.lesson.CreateLessonRequest;
import com.example.learning_api.dto.request.lesson.UpdateLessonRequest;


public interface ILessonService {
    void createLesson(CreateLessonRequest createLessonRequest);
    void updateLesson(UpdateLessonRequest updateLessonRequest);
    void deleteLesson(String id);
}
