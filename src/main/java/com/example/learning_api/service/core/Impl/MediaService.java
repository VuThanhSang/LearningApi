package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.media.CreateMediaRequest;
import com.example.learning_api.dto.request.media.UpdateMediaRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.lesson.GetMediaResponse;
import com.example.learning_api.entity.sql.database.MediaEntity;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.MediaRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IMediaService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService implements IMediaService {
    private final ModelMapperService modelMapperService;
    private final LessonRepository lessonRepository;
    private final CloudinaryService cloudinaryService;
    private final MediaRepository mediaRepository;
    @Override
    public void createMedia(CreateMediaRequest body) {
        try{
            if (body.getLessonId()==null){
                throw new IllegalArgumentException("LessonId is required");
            }
            if (lessonRepository.findById(body.getLessonId()).isEmpty()){
                throw new IllegalArgumentException("LessonId is not found");
            }
            MediaEntity mediaEntity = modelMapperService.mapClass(body, MediaEntity.class);
            if (body.getFile() != null) {
                byte[] fileBytes = body.getFile().getBytes();
                String fileType = body.getFile().getOriginalFilename().substring(body.getFile().getOriginalFilename().lastIndexOf("."));
                CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "Media") + fileType,
                        fileBytes,
                        "video"
                );
                mediaEntity.setFilePath(fileUploaded.getUrl());
            }
            mediaEntity.setCreatedAt(new Date());
            mediaEntity.setUpdatedAt(new Date());
            mediaRepository.save(mediaEntity);
        }
        catch (Exception e) {
            log.error("Error in createMedia: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteMedia(String mediaId) {
        try{
            if (mediaRepository.findById(mediaId ).isEmpty()){
                throw new IllegalArgumentException("MediaId is not found");
            }
            mediaRepository.deleteById(mediaId);
        }
        catch (Exception e) {
            log.error("Error in deleteMedia: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void updateMedia(UpdateMediaRequest body) {
        try{
            MediaEntity mediaEntity= mediaRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (body.getId()==null){
                throw new IllegalArgumentException("Id is required");
            }
            if (mediaEntity==null){
                throw new IllegalArgumentException("Media is not found");
            }
            if (body.getFile() != null) {
                byte[] fileBytes = body.getFile().getBytes();
                String fileType = body.getFile().getOriginalFilename().substring(body.getFile().getOriginalFilename().lastIndexOf("."));
                CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "Media") + fileType,
                        fileBytes,
                        "video"
                );
                mediaEntity.setFilePath(fileUploaded.getUrl());
            }
            if (body.getName()!=null){
                mediaEntity.setName(body.getName());
            }
            if (body.getDescription()!=null){
                mediaEntity.setDescription(body.getDescription());
            }
            mediaRepository.save(mediaEntity);
        }
        catch (Exception e) {
            log.error("Error in updateMedia: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public MediaEntity getMedia(String mediaId) {
        try{
            return mediaRepository.findById(mediaId).orElseThrow(()->new IllegalArgumentException("Id is not found"));
        }
        catch (Exception e) {
            log.error("Error in getMedia: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetMediaResponse getMediaByLessonId(String lessonId, Integer page, Integer size) {
       try {
              Pageable pageAble = PageRequest.of(page, size);
              Page<MediaEntity> mediaEntities = mediaRepository.findByLessonId(lessonId, pageAble);
              GetMediaResponse getMediaResponse = new GetMediaResponse();
              List<GetMediaResponse.MediaResponse> mediaResponses = new ArrayList<>();
                for (MediaEntity mediaEntity : mediaEntities) {
                    GetMediaResponse.MediaResponse mediaResponse = modelMapperService.mapClass(mediaEntity, GetMediaResponse.MediaResponse.class);
                    mediaResponses.add(mediaResponse);
                }
                getMediaResponse.setMedia(mediaResponses);
                getMediaResponse.setTotalPage(mediaEntities.getTotalPages());
                getMediaResponse.setTotalElements(mediaEntities.getTotalElements());

                return getMediaResponse;

       }
         catch (Exception e) {
              log.error("Error in getMediaByLessonId: ", e);
              throw new IllegalArgumentException(e.getMessage());
         }
    }
}
