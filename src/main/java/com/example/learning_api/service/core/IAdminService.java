package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.admin.ChangeRoleRequest;
import com.example.learning_api.dto.response.admin.GetAdminDashboardResponse;

public interface IAdminService {
    void changeRole(ChangeRoleRequest body);
    void deleteAccount(String userId);
    void removeFile(String fileId);
    GetAdminDashboardResponse getAdminDashboard();

}
