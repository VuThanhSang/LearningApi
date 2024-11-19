package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.deadline.CreateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineSubmissionsRequest;
import com.example.learning_api.dto.response.deadline.DeadlineSubmissionResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlineSubmissionsResponse;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface IDeadlineSubmissionsService {
    void CreateDeadlineSubmissions(CreateDeadlineSubmissionsRequest body);
    void UpdateDeadlineSubmissions(UpdateDeadlineSubmissionsRequest body);
    void DeleteDeadlineSubmissions(String id);
    void GradeDeadlineSubmissions(String id, String grade,String feedback);
    DeadlineSubmissionResponse GetDeadlineSubmissions(String id);
    GetDeadlineSubmissionsResponse GetDeadlineSubmissionsByDeadlineId( String deadlineId, Integer page, Integer size, String search, String status,
                                                                       String sortBy, Sort.Direction sortDirection);
    GetDeadlineSubmissionsResponse GetDeadlineSubmissionsByStudentId(String studentId,String deadlineId, Integer page, Integer size);
    byte[] downloadDeadlineSubmissionsByStudentId(String deadlineId);
    List<String> downloadSubmission(String deadlineId, DeadlineSubmissionStatus type);
}
