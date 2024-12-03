package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.admin.ChangeRoleRequest;
import com.example.learning_api.dto.response.admin.GetAdminDashboardResponse;
import com.example.learning_api.dto.response.admin.GetClassRoomsAdminResponse;
import com.example.learning_api.dto.response.admin.GetUserDetailResponse;
import com.example.learning_api.dto.response.admin.GetUsersResponse;
import com.example.learning_api.dto.response.classroom.GetClassRoomsResponse;
import com.example.learning_api.dto.response.student.GetStudentsResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;

public interface IAdminService {
    void changeRole(ChangeRoleRequest body);
    void deleteAccount(String userId);
    void blockAccount(String userId);
    void removeFile(String fileId);
    void updateStatus(String userId, String status);
    void updateForumStatus(String forumId, String status);
    GetAdminDashboardResponse getAdminDashboard();

    GetUsersResponse getTeachers(String search, int page, int size, String sort, String order, String status);
    GetUsersResponse getStudents(String search, int page, int size, String sort, String order, String status);
    GetClassRoomsAdminResponse getClassRooms(String search, int page, int size, String sort, String order, String status);
    GetUserDetailResponse getUserDetail(String userId);
}
