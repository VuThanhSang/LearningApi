package com.example.learning_api.dto.request.image;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadImageRequest {
    List<MultipartFile> images;
}
