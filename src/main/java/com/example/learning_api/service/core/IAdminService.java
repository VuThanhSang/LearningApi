package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.admin.ChangeRoleRequest;
import com.example.learning_api.dto.response.admin.GetAdminDashboardResponse;
import com.example.learning_api.dto.response.admin.GetUsersResponse;
import com.example.learning_api.dto.response.student.GetStudentsResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;

public interface IAdminService {
    void changeRole(ChangeRoleRequest body);
    void deleteAccount(String userId);
    void blockAccount(String userId);
    void removeFile(String fileId);
    GetAdminDashboardResponse getAdminDashboard();

    GetUsersResponse getTeachers(String search, int page, int size, String sort, String order, String status);
    GetUsersResponse getStudents(String search, int page, int size, String sort, String order, String status);

}
