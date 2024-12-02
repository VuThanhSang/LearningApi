package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.faq.CreateFaqRequest;
import com.example.learning_api.dto.request.faq.UpdateFaqRequest;
import com.example.learning_api.dto.response.faq.GetFaqDetailResponse;
import com.example.learning_api.dto.response.faq.GetFaqsResponse;

public interface IFaqService {
    void createFaq(CreateFaqRequest createFaqRequest);
    void updateFaq(UpdateFaqRequest updateFaqRequest);
    void deleteFaq(String id);
//    GetFaqDetailResponse getFaqWithResourcesAndMediaAndSubstances(String id);
    GetFaqDetailResponse getFaqDetail(String id);
    GetFaqsResponse getFaqs(Integer page, Integer size, String search, String sort, String order);

}
