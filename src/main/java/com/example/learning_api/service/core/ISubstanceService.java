package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.substance.CreateSubstanceRequest;
import com.example.learning_api.dto.request.substance.UpdateSubstanceRequest;
import com.example.learning_api.dto.response.lesson.GetSubstancesResponse;
import com.example.learning_api.entity.sql.database.SubstanceEntity;

public interface ISubstanceService {
    void createSubstance(CreateSubstanceRequest body);
    void deleteSubstance(String substanceId);
    void updateSubstance(UpdateSubstanceRequest body);
    SubstanceEntity getSubstance(String substanceId);
    GetSubstancesResponse getSubstancesByLessonId(String lessonId, Integer page, Integer size);
}
