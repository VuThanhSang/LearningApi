package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.image.RemoveImageRequest;
import com.example.learning_api.dto.request.image.UploadImageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    List<String> uploadImage(UploadImageRequest images);
    void deleteImage(RemoveImageRequest imageUrl);
}
