package com.example.learning_api.service.core.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.resource.CreateResourceRequest;
import com.example.learning_api.dto.request.resource.UpdateResourceRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.entity.sql.database.ResourceEntity;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.ResourceRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IResourceService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService implements IResourceService {
    private final ModelMapperService modelMapperService;
    private final LessonRepository lessonRepository;
    private final CloudinaryService cloudinaryService;
    private final ResourceRepository resourceRepository;
    private final Cloudinary cloudinary;
    @Override
   public void createResource(CreateResourceRequest body) {
    try{
        if (body.getLessonId()==null){
            throw new IllegalArgumentException("LessonId is required");
        }
        if (lessonRepository.findById(body.getLessonId()).isEmpty()){
            throw new IllegalArgumentException("LessonId is not found");
        }
        ResourceEntity resourceEntity = modelMapperService.mapClass(body, ResourceEntity.class);
        if (body.getFile() != null) {
            byte[] fileBytes = body.getFile().getBytes();
            String fileType = body.getFile().getOriginalFilename().substring(body.getFile().getOriginalFilename().lastIndexOf("."));
            CloudinaryUploadResponse response = cloudinaryService.uploadFileToFolder(
                    CloudinaryConstant.CLASSROOM_PATH,
                    StringUtils.generateFileName(body.getName(), "Resource") + fileType,
                    fileBytes,
                    "raw"
            );
            resourceEntity.setFilePath(response.getSecureUrl());
        }
        resourceEntity.setCreatedAt(new Date());
        resourceEntity.setCreatedAt(new Date());
        resourceRepository.save(resourceEntity);
    }
    catch (Exception e){
        log.error(e.getMessage());
        throw new IllegalArgumentException(e.getMessage());
    }
}

    @Override
    public void deleteResource(String resourceId) {
        try{
            if (resourceRepository.findById(resourceId).isEmpty()){
                throw new IllegalArgumentException("ResourceId is not found");
            }
            resourceRepository.deleteById(resourceId);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void updateResource(UpdateResourceRequest body) {
        try{
            ResourceEntity resourceEntity = resourceRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (body.getId()==null){
                throw new IllegalArgumentException("Id is required");
            }
            if (resourceEntity==null){
                throw new IllegalArgumentException("Id is not found");
            }
            if (body.getName()!=null){
                resourceEntity.setName(body.getName());
            }
            if (body.getFile()!=null){
                byte[] fileBytes = body.getFile().getBytes();
                String fileType = body.getFile().getOriginalFilename().substring(body.getFile().getOriginalFilename().lastIndexOf("."));
                CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom") + fileType,
                        fileBytes,"raw"
                );
                resourceEntity.setFilePath(fileUploaded.getUrl());
            }
            resourceEntity.setUpdatedAt(new Date());
            resourceRepository.save(resourceEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}