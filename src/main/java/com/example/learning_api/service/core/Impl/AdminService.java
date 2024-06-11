package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.response.admin.GetAdminDashboardResponse;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.UserStatus;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.core.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {

    private final UserRepository userRepository;




    @Override
    public void changeRole(String userId, String role) {
        try {
            userRepository.findById(userId).ifPresent(userEntity -> {
                userEntity.setRole(RoleEnum.valueOf(role));
                userRepository.save(userEntity);
            });
        } catch (Exception e) {
            log.error("Error when change role", e);
        }

    }

    @Override
    public void deleteAccount(String userId) {
        try {
            userRepository.findById(userId).ifPresent(userEntity -> {
                userEntity.setStatus(UserStatus.BLOCKED);
                userRepository.save(userEntity);
            });
        } catch (Exception e) {
            log.error("Error when delete account", e);
        }

    }

    @Override
    public GetAdminDashboardResponse getAdminDashboard() {
        return null;
    }
}
