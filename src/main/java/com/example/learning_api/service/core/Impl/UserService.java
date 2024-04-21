package com.example.learning_api.service.core.Impl;

import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.core.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
