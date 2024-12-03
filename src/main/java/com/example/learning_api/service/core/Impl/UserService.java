package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.user.UpdateUserRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.core.IUserService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity updateUser(UpdateUserRequest userEntity) {
        try {
            UserEntity user = userRepository.findById(userEntity.getId()).orElseThrow(() -> new RuntimeException("User not found"));

            if (userEntity.getEmail() != null) {
                user.setEmail(userEntity.getEmail());
            }
            if (userEntity.getFullname() != null) {
                user.setFullname(userEntity.getFullname());
            }
            if (userEntity.getRole() != null) {
                user.setRole(userEntity.getRole());
            }
            if (userEntity.getStatus() != null) {
                user.setStatus(userEntity.getStatus());
            }
            if (userEntity.getAvatar() != null) {
                user.setAvatar(userEntity.getAvatar());
            }
            if (userEntity.getSource()!=null){
                byte[] originalImage = new byte[0];
                originalImage = userEntity.getSource().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(user.getFullname(), "classroom"),
                        newImage,
                        "image"
                );
                user.setAvatar(imageUploaded.getUrl());
            }

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
