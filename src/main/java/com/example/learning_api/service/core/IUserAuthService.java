package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.LoginUserRequest;
import com.example.learning_api.dto.request.RegisterUserRequest;
import com.example.learning_api.dto.response.LoginResponse;
import com.example.learning_api.dto.response.RegisterResponse;

public interface IUserAuthService {
    RegisterResponse registerUser(RegisterUserRequest body);
    LoginResponse loginUser(LoginUserRequest body);
}
