package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.lesson.CreateLessonRequest;
import com.example.learning_api.dto.request.lesson.UpdateLessonRequest;
import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.entity.sql.database.LessonEntity;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.SectionRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ILessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            Integer index = lessonRepository.findMaxIndexBySectionId(createLessonRequest.getSectionId());
            lessonEntity.setIndex(index==null?0:index+1);
            lessonEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            lessonEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
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
            lessonEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
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

    @Override
    public GetLessonDetailResponse getLessonWithResourcesAndMediaAndSubstances(String id) {
        try {
            GetLessonDetailResponse getLessonDetailResponse = lessonRepository.getLessonWithResourcesAndMediaAndSubstances(id);
            if (getLessonDetailResponse==null){
                throw new IllegalArgumentException("Lesson not found");
            }
            return getLessonDetailResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<GetLessonDetailResponse> getLessonBySectionId(String sectionId) {
        try{
            List<LessonEntity> lessonEntities = lessonRepository.findBySectionId(sectionId, Sort.by(Sort.Direction.ASC, "index"));
            List<GetLessonDetailResponse> getLessonDetailResponses = new ArrayList<>();
            for (LessonEntity lessonEntity: lessonEntities){
                getLessonDetailResponses.add(lessonRepository.getLessonWithResourcesAndMediaAndSubstances(lessonEntity.getId()));
            }
            return getLessonDetailResponses;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
