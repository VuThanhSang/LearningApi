package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.request.discussion.CreateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.CreateDiscussionRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionDetailResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionsResponse;
import com.example.learning_api.entity.sql.database.DiscussionCommentEntity;
import com.example.learning_api.entity.sql.database.DiscussionEntity;
import com.example.learning_api.enums.DiscussionStatus;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.repository.database.DiscussionCommentRepository;
import com.example.learning_api.repository.database.DiscussionRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDiscussionService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscussionService implements IDiscussionService {
    private final DiscussionRepository discussionRepository;
    private final DiscussionCommentRepository discussionCommentRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    @Override
    public void createDiscussion(CreateDiscussionRequest request) {
        try{
            if (request.getRole() =="USER"){
                if (studentRepository.findById(request.getAuthorId()).isEmpty()){
                    throw new IllegalArgumentException("Student Id is not found");
                }
            }
            else if (request.getRole() =="TEACHER"){
                if (teacherRepository.findById(request.getAuthorId()).isEmpty()){
                    throw new IllegalArgumentException("Teacher Id is not found");
                }
            }
            DiscussionEntity discussionEntity = modelMapperService.mapClass(request, DiscussionEntity.class);

            if (request.getSource()!=null){
                byte[] originalImage = new byte[0];
                originalImage = request.getSource().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(request.getTitle(), "discussion"),
                        newImage,
                        "image"
                );
                discussionEntity.setImage(imageUploaded.getUrl());
            }
            discussionEntity.setCommentCount(0);
            discussionEntity.setUpvote(0);
            discussionEntity.setDownvote(0);
            discussionEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            discussionEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            discussionRepository.save(discussionEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateDiscussion(UpdateDiscussionRequest request) {
        try{
            DiscussionEntity discussionEntity = discussionRepository.findById(request.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (request.getTitle()!=null){
                discussionEntity.setTitle(request.getTitle());
            }
            if (request.getContent()!=null){
                discussionEntity.setContent(request.getContent());
            }
            if (request.getStatus()!=null){
                discussionEntity.setStatus(DiscussionStatus.valueOf(request.getStatus()));
            }


            if (request.getSource()!=null){
                byte[] originalImage = new byte[0];
                originalImage = request.getSource().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(request.getTitle(), "discussion"),
                        newImage,
                        "image"
                );
                discussionEntity.setImage(imageUploaded.getUrl());
            }
            discussionEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            discussionRepository.save(discussionEntity);

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteDiscussion(String id) {
        try{
            DiscussionEntity discussionEntity = discussionRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            discussionRepository.delete(discussionEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetDiscussionsResponse getDiscussions(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<DiscussionEntity> discussionEntities = discussionRepository.findAllByTitleContaining(search, pageAble);
            GetDiscussionsResponse getDiscussionsResponse = new GetDiscussionsResponse();
            List<GetDiscussionsResponse.DiscussionResponse> data = new ArrayList<>();
            discussionEntities.forEach(discussionEntity -> {
                GetDiscussionsResponse.DiscussionResponse discussionResponse = GetDiscussionsResponse.DiscussionResponse.formDiscussionEntity(discussionEntity);
                data.add(discussionResponse);
            });
            getDiscussionsResponse.setDiscussions(data);
            getDiscussionsResponse.setTotalElements(discussionEntities.getTotalElements());
            getDiscussionsResponse.setTotalPage(discussionEntities.getTotalPages());
            return getDiscussionsResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetDiscussionDetailResponse getDiscussionDetail(String id) {
        try{
            DiscussionEntity discussionEntity = discussionRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            GetDiscussionDetailResponse getDiscussionDetailResponse = modelMapperService.mapClass(discussionEntity, GetDiscussionDetailResponse.class);
            List<DiscussionCommentEntity> discussionCommentEntities = discussionCommentRepository.findByDiscussionId(id);
            List<GetDiscussionDetailResponse.DiscussionComment> discussionComments = new ArrayList<>();
            discussionCommentEntities.forEach(discussionCommentEntity -> {
                GetDiscussionDetailResponse.DiscussionComment discussionComment = modelMapperService.mapClass(discussionCommentEntity, GetDiscussionDetailResponse.DiscussionComment.class);
                if (String.valueOf(discussionCommentEntity.getRole())== "USER"){
                    discussionComment.setStudent(studentRepository.findById(discussionCommentEntity.getAuthorId()).get());
                }
                else{
                    discussionComment.setTeacher(teacherRepository.findById(discussionCommentEntity.getAuthorId()).get());
                }
                discussionComments.add(discussionComment);
            });
            getDiscussionDetailResponse.setComments(discussionComments);
            return getDiscussionDetailResponse;

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void createDiscussionComment(CreateDiscussionCommentRequest request) {
        try{
            if (request.getRole()== "USER"){
                if (studentRepository.findById(request.getAuthorId()).isEmpty()){
                    throw new IllegalArgumentException("Student Id is not found");
                }
            }
            else if (request.getRole()== "TEACHER"){
                if (teacherRepository.findById(request.getAuthorId()).isEmpty()){
                    throw new IllegalArgumentException("Teacher Id is not found");
                }
            }
            DiscussionEntity discussionEntity = discussionRepository.findById(request.getDiscussionId()).orElseThrow(()->new IllegalArgumentException("Discussion Id is not found"));
            if (request.getAuthorId()==null){
                throw new IllegalArgumentException("Author Id is required");
            }
            DiscussionCommentEntity discussionCommentEntity = modelMapperService.mapClass(request, DiscussionCommentEntity.class);
            if (request.getSource()!=null){
                byte[] originalImage = new byte[0];
                originalImage = request.getSource().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(request.getContent(), "discussion_comment"),
                        newImage,
                        "image"
                );
                discussionCommentEntity.setImage(imageUploaded.getUrl());
            }
            discussionCommentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            discussionCommentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            discussionCommentEntity.setReplyCount(0);
            discussionCommentEntity.setUpvote(0);
            discussionCommentEntity.setDownvote(0);
            discussionCommentRepository.save(discussionCommentEntity);

            discussionEntity.setCommentCount(discussionEntity.getCommentCount()+1);
            discussionRepository.save(discussionEntity);

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateDiscussionComment(UpdateDiscussionCommentRequest request) {
        try {
            DiscussionCommentEntity discussionCommentEntity = discussionCommentRepository.findById(request.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (request.getContent()!=null){
                discussionCommentEntity.setContent(request.getContent());
            }
            if (request.getUpvote()!=0){
                discussionCommentEntity.setUpvote(request.getUpvote());
            }
            if (request.getDownvote()!=0){
                discussionCommentEntity.setDownvote(request.getDownvote());
            }
            if (request.getReplyCount()!=0){
                discussionCommentEntity.setReplyCount(request.getReplyCount());
            }
            if (request.getStatus()!=null){
                discussionCommentEntity.setStatus(DiscussionStatus.valueOf(request.getStatus()));
            }
            if (request.getSource()!=null){
                byte[] originalImage = new byte[0];
                originalImage = request.getSource().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(request.getContent(), "discussion_comment"),
                        newImage,
                        "image"
                );
                discussionCommentEntity.setImage(imageUploaded.getUrl());
            }
            discussionCommentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            discussionCommentRepository.save(discussionCommentEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteDiscussionComment(String id) {
        try{
            DiscussionCommentEntity discussionCommentEntity = discussionCommentRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            discussionCommentRepository.delete(discussionCommentEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}
