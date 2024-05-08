package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.answer.CreateAnswerRequest;
import com.example.learning_api.dto.request.answer.UpdateAnswerRequest;
import com.example.learning_api.dto.response.answer.CreateAnswerResponse;

public interface IAnswerService {
    CreateAnswerResponse createAnswer(CreateAnswerRequest body);
    void updateAnswer(UpdateAnswerRequest body);
    void deleteAnswer(String id);

}
