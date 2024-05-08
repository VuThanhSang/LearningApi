package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.substance.CreateSubstanceRequest;
import com.example.learning_api.dto.request.substance.UpdateSubstanceRequest;

public interface ISubstanceService {
    void createSubstance(CreateSubstanceRequest body);
    void deleteSubstance(String substanceId);
    void updateSubstance(UpdateSubstanceRequest body);
}
