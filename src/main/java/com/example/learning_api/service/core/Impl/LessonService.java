package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.lesson.CreateLessonRequest;
import com.example.learning_api.dto.request.lesson.UpdateLessonRequest;
import com.example.learning_api.entity.sql.database.LessonEntity;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.SectionRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ILessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonService implements ILessonService {
    private final LessonRepository lessonRepository;
    private final ModelMapperService modelMapperService;
    private final SectionRepository sectionRepository;
    @Override
    public void createLesson(CreateLessonRequest createLessonRequest) {
        try{
            if (createLessonRequest.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (createLessonRequest.getSectionId()==null){
                throw new IllegalArgumentException("SectionId is required");
            }
            if (sectionRepository.findById(createLessonRequest.getSectionId()).isEmpty()){
                throw new IllegalArgumentException("SectionId is not found");
            }
            LessonEntity lessonEntity = modelMapperService.mapClass(createLessonRequest, LessonEntity.class);
            lessonEntity.setCreatedAt(new Date());
            lessonEntity.setUpdatedAt(new Date());
            lessonRepository.save(lessonEntity);

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateLesson(UpdateLessonRequest updateLessonRequest) {
        try {
            LessonEntity lessonEntity = lessonRepository.findById(updateLessonRequest.getId()).orElseThrow(()->new IllegalArgumentException("Lesson not found"));
            if (updateLessonRequest.getName()!=null)
                lessonEntity.setName(updateLessonRequest.getName());
            if (updateLessonRequest.getDescription()!=null)
                lessonEntity.setDescription(updateLessonRequest.getDescription());
            lessonEntity.setUpdatedAt(new Date());
            lessonRepository.save(lessonEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteLesson(String id) {
        try {
            lessonRepository.deleteById(id);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
