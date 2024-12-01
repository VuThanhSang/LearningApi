package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.lesson.CreateLessonRequest;
import com.example.learning_api.dto.request.lesson.UpdateLessonRequest;
import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;

import java.util.List;


public interface ILessonService {
    void createLesson(CreateLessonRequest createLessonRequest);
    void updateLesson(UpdateLessonRequest updateLessonRequest);
    void deleteLesson(String id);
    GetLessonDetailResponse getLessonWithResourcesAndMediaAndSubstances(String id);
    List<GetLessonDetailResponse> getLessonBySectionId(String sectionId,String role);
}
