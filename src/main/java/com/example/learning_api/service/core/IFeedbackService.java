package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.feedback.CreateFeedbackAnswerRequest;
import com.example.learning_api.dto.request.feedback.CreateFeedbackRequest;
import com.example.learning_api.dto.request.feedback.UpdateFeedbackRequest;
import com.example.learning_api.dto.response.feedback.GetFeedBacksResponse;
import com.example.learning_api.dto.response.feedback.FeedbackAnswerResponse;
import com.example.learning_api.entity.sql.database.FeedbackEntity;

import java.util.List;

public interface IFeedbackService {
    void createFeedback(CreateFeedbackRequest body);
    void updateFeedback(UpdateFeedbackRequest body);
    void deleteFeedback(String testFeedbackId);
    void createFeedbackAnswer(CreateFeedbackAnswerRequest body);
    void updateFeedbackAnswer(String testFeedbackAnswerId, String answer);
    void deleteFeedbackAnswer(String testFeedbackAnswerId);
    FeedbackEntity getFeedbackById(String testFeedbackId);
    List<FeedbackEntity> getFeedbacksByStudentIdAndTestId(String studentId, String formId,String formType);
    GetFeedBacksResponse getFeedbacksByTestId(String formId,String formType, String sort, int page, int size);
    List<FeedbackAnswerResponse> getFeedbackAnswersByFeedbackId(String testFeedbackId);

}
