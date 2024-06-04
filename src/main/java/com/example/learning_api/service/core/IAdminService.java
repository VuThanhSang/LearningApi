package com.example.learning_api.service.core;

import com.example.learning_api.dto.response.admin.GetAdminDashboardResponse;

public interface IAdminService {
    void changeRole(String userId, String role);
    void deleteAccount(String userId);
    GetAdminDashboardResponse getAdminDashboard();

}
