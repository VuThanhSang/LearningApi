package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.summary.CreateSummaryRequest;
import com.example.learning_api.dto.request.summary.UpdateSummaryRequest;
import com.example.learning_api.dto.response.summary.GetSummaryResponse;
import com.example.learning_api.entity.sql.database.SummaryEntity;

import java.util.List;

public interface ISummaryService {
    void createSummary(CreateSummaryRequest body);
    void updateSummary(UpdateSummaryRequest body);
    void deleteSummary(String id);
    SummaryEntity getSummary(String id);
    List<GetSummaryResponse> getSummariesByStudentId(String studentId);
    List<GetSummaryResponse> getSummariesByStudentIdAndTermId(String studentId, String termId);
//    void getSummariesByTermIdAndCourseId(String termId, String courseId);
}
