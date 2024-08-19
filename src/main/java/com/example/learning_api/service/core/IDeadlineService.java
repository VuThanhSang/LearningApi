package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.deadline.CreateDeadlineRequest;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineRequest;
import com.example.learning_api.dto.response.classroom.ClassroomDeadlineResponse;
import com.example.learning_api.dto.response.deadline.DeadlineResponse;
import com.example.learning_api.dto.response.deadline.GetDeadlinesResponse;
import com.example.learning_api.dto.response.deadline.UpcomingDeadlinesResponse;
import com.example.learning_api.entity.sql.database.DeadlineEntity;
import org.apache.commons.collections4.Get;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface IDeadlineService {
    void createDeadline(CreateDeadlineRequest createDeadlineRequest);
    void updateDeadline(UpdateDeadlineRequest updateDeadlineRequest);
    void deleteDeadline(String deadlineId);
    DeadlineResponse getDeadline(String deadlineId);
    GetDeadlinesResponse getDeadlinesByLessonId(String classroomId, Integer page, Integer size);
    List<UpcomingDeadlinesResponse> getUpcomingDeadlineByStudentId(String studentId,String date);
    ClassroomDeadlineResponse getClassroomDeadlinesByClassroomId(String classroomId, Integer page, Integer size);
    GetDeadlinesResponse getDeadlinesByTeacherId(String teacherId, String search,String status,String startDate,String enDate,Integer page, Integer size);
    GetDeadlinesResponse getDeadlinesByStudentId(String studentId, String search,String status,String startDate,String enDate,String classroomId,Integer page, Integer size, String sort, Sort.Direction order);
}
