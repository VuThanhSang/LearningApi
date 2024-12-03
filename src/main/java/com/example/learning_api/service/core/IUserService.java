package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.user.UpdateUserRequest;
import com.example.learning_api.entity.sql.database.UserEntity;

public interface IUserService {
    UserEntity updateUser(UpdateUserRequest userEntity);
}
