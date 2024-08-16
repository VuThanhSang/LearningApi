package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.deadline.CreateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.response.deadline.GetDeadlineSubmissionsResponse;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;

public interface IDeadlineSubmissionsService {
    void CreateDeadlineSubmissions(CreateDeadlineSubmissionsRequest body);
    void UpdateDeadlineSubmissions(UpdateDeadlineSubmissionsRequest body);
    void DeleteDeadlineSubmissions(String id);
    void GradeDeadlineSubmissions(String id, String grade,String feedback);
    DeadlineSubmissionsEntity GetDeadlineSubmissions(String id);
    GetDeadlineSubmissionsResponse GetDeadlineSubmissionsByDeadlineId(String deadlineId, Integer page, Integer size);
    GetDeadlineSubmissionsResponse GetDeadlineSubmissionsByStudentId(String studentId,String deadlineId, Integer page, Integer size);

}
