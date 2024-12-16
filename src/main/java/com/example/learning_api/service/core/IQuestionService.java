package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.question.CreateQuestionRequest;
import com.example.learning_api.dto.request.question.UpdateQuestionRequest;
import com.example.learning_api.dto.response.question.CreateQuestionResponse;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;

import java.util.List;

public interface IQuestionService {
    CreateQuestionResponse createQuestion(CreateQuestionRequest body);
    void updateQuestion(UpdateQuestionRequest body);
    void deleteQuestion(String id);
    void deleteQuestions(String[] ids);
    GetQuestionsResponse getQuestions(int page, int size, String search);
    List<GetQuestionsResponse.QuestionResponse> getQuestionsByTestId(String testId,String role);
}
