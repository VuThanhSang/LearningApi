package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.faq.CreateFaqRequest;
import com.example.learning_api.dto.request.faq.UpdateFaqRequest;

public interface IFaqService {
    void createFaq(CreateFaqRequest createFaqRequest);
    void updateFaq(UpdateFaqRequest updateFaqRequest);
    void deleteFaq(String id);
//    GetFaqDetailResponse getFaqWithResourcesAndMediaAndSubstances(String id);
}
