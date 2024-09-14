package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.image.RemoveImageRequest;
import com.example.learning_api.dto.request.image.UploadImageRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.core.IImageService;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService implements IImageService {

    private final CloudinaryService cloudinaryService;

    @Override
    public List<String> uploadImage(UploadImageRequest images) {
        try{
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : images.getImages()) {
                byte[] fileBytes = file.getBytes();
                String fileName = StringUtils.generateFileName(file.getOriginalFilename(), "Image for document");
                CloudinaryUploadResponse response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, fileBytes, "image");
                urls.add(response.getUrl());
            }
            return urls;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void deleteImage(RemoveImageRequest imageUrl) {
        try{
            cloudinaryService.deleteImage(imageUrl.getUrl());
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
