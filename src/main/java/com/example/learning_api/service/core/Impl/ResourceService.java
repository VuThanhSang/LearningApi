package com.example.learning_api.service.core.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.resource.CreateResourceRequest;
import com.example.learning_api.dto.request.resource.UpdateResourceRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.lesson.GetResourceResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.LessonEntity;
import com.example.learning_api.entity.sql.database.ResourceEntity;
import com.example.learning_api.entity.sql.database.SectionEntity;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.ResourceRepository;
import com.example.learning_api.repository.database.SectionRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IResourceService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService implements IResourceService {
    private final ModelMapperService modelMapperService;
    private final LessonRepository lessonRepository;
    private final CloudinaryService cloudinaryService;
    private final ResourceRepository resourceRepository;
    private final SectionRepository sectionRepository;
    private final ClassRoomRepository classRoomRepository;
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
        LessonEntity lessonEntity = lessonRepository.findById(body.getLessonId()).get();
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
            resourceEntity.setFileName(body.getFile().getOriginalFilename());
            resourceEntity.setFileExtension(fileType);
            resourceEntity.setFileSize(body.getFile().getSize() + " bytes");
            resourceEntity.setFileType(body.getFile().getContentType());
            resourceEntity.setFilePath(response.getSecureUrl());
        }
        resourceEntity.setCreatedAt(new Date());
        resourceEntity.setCreatedAt(new Date());
        resourceRepository.save(resourceEntity);
        SectionEntity sectionEntity = sectionRepository.findById(lessonEntity.getSectionId()).get();
        ClassRoomEntity classRoomEntity = classRoomRepository.findById(sectionEntity.getClassRoomId()).get();
        classRoomEntity.setTotalResource(classRoomEntity.getTotalResource()+1);
        classRoomRepository.save(classRoomEntity);

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
            LessonEntity lessonEntity = lessonRepository.findById(resourceId).get();
            SectionEntity sectionEntity = sectionRepository.findById(lessonEntity.getSectionId()).get();
            ClassRoomEntity classRoomEntity = classRoomRepository.findById(sectionEntity.getClassRoomId()).get();
            classRoomEntity.setTotalResource(classRoomEntity.getTotalResource()+1);
            classRoomRepository.save(classRoomEntity);
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

    @Override
    public ResourceEntity getResource(String resourceId) {
        try{
            return resourceRepository.findById(resourceId).orElseThrow(()->new IllegalArgumentException("Id is not found"));
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetResourceResponse getResourceByLessonId(String lessonId, Integer page, Integer size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ResourceEntity> resourceEntities = resourceRepository.findByLessonId(lessonId, pageAble);
            GetResourceResponse response = new GetResourceResponse();
            List<GetResourceResponse.ResourceResponse> resourceResponses = new ArrayList<>();
            for (ResourceEntity resourceEntity: resourceEntities){
                GetResourceResponse.ResourceResponse resourceResponse = modelMapperService.mapClass(resourceEntity, GetResourceResponse.ResourceResponse.class);
                GetResourceResponse.FileResponse fileResponse = new GetResourceResponse.FileResponse();
                fileResponse.setUrl(resourceEntity.getFilePath());
                fileResponse.setFileType(resourceEntity.getFileType());
                fileResponse.setFileName(resourceEntity.getFileName());
                fileResponse.setFileSize(resourceEntity.getFileSize());
                resourceResponse.setFile(fileResponse);
                resourceResponses.add(resourceResponse);
            }
            response.setResources(resourceResponses);
            response.setTotalPage(resourceEntities.getTotalPages());
            response.setTotalElements(resourceEntities.getTotalElements());
            return response;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}