package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.request.discussion.CreateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.CreateDiscussionRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionCommentResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionDetailResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionsResponse;
import com.example.learning_api.entity.sql.database.DiscussionCommentEntity;
import com.example.learning_api.entity.sql.database.DiscussionEntity;
import com.example.learning_api.entity.sql.database.FAQEntity;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            discussionEntity.setSources(new ArrayList<>());
            processSources(request.getSources(), request.getTitle(), discussionEntity);
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
    public void processSources(List<SourceUploadDto> sources, String question, DiscussionEntity discussionEntity) {
        if (sources.isEmpty()) {
            return;
        }

        for (SourceUploadDto source : sources) {
            try {
                DiscussionEntity.SourceDto sourceDto = processSource(source, question);
                discussionEntity.getSources().add(sourceDto);
            } catch (IOException e) {
                log.error("Error processing source: " + e.getMessage());
                throw new IllegalArgumentException("Error processing source");
            }
        }
    }

    private DiscussionEntity.SourceDto processSource(SourceUploadDto source, String question) throws IOException {
        DiscussionEntity.SourceDto sourceDto = new DiscussionEntity.SourceDto();
        sourceDto.setType(source.getType());

        byte[] fileBytes = source.getPath().getBytes();
        String fileName = StringUtils.generateFileName(question, "forum");

        CloudinaryUploadResponse response;
        switch (source.getType()) {
            case IMAGE:
                byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
                response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
                break;
            case VIDEO:
                String videoFileType = getFileExtension(source.getPath().getOriginalFilename());
                response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
                break;
            case DOCUMENT:
                String docFileType = getFileExtension(source.getPath().getOriginalFilename());
                response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
                break;
            default:
                throw new IllegalArgumentException("Unsupported source type");
        }

        sourceDto.setPath(response.getUrl() );
        return sourceDto;
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
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

            if (request.getSources()!=null) {
                discussionEntity.getSources().clear();
                processSources(request.getSources(), request.getTitle(), discussionEntity);
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
            discussionCommentRepository.deleteByDiscussionId(id);
            discussionRepository.delete(discussionEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void upvoteDiscussion(String id) {
        try{
            DiscussionEntity discussionEntity = discussionRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            discussionEntity.setUpvote(discussionEntity.getUpvote()+1);
            discussionRepository.save(discussionEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void downvoteDiscussion(String id) {
        try{
            DiscussionEntity discussionEntity = discussionRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            discussionEntity.setDownvote(discussionEntity.getDownvote()+1);
            discussionRepository.save(discussionEntity);
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
                discussionResponse.setSources(discussionEntity.getSources());
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
    public GetDiscussionsResponse getDiscussionByAuthor(String authorId, int page, int size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<DiscussionEntity> discussionEntities = discussionRepository.findByAuthorId(authorId, pageAble);
            GetDiscussionsResponse getDiscussionsResponse = new GetDiscussionsResponse();
            List<GetDiscussionsResponse.DiscussionResponse> data = new ArrayList<>();
            discussionEntities.forEach(discussionEntity -> {
                GetDiscussionsResponse.DiscussionResponse discussionResponse = GetDiscussionsResponse.DiscussionResponse.formDiscussionEntity(discussionEntity);
                discussionResponse.setSources(discussionEntity.getSources());
                discussionResponse.setTags(discussionEntity.getTags());
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
    public GetDiscussionsResponse getDiscussionByTag(String tag, int page, int size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<DiscussionEntity> discussionEntities = discussionRepository.findByTagsContaining(tag, pageAble);
            GetDiscussionsResponse getDiscussionsResponse = new GetDiscussionsResponse();
            List<GetDiscussionsResponse.DiscussionResponse> data = new ArrayList<>();
            discussionEntities.forEach(discussionEntity -> {
                GetDiscussionsResponse.DiscussionResponse discussionResponse = GetDiscussionsResponse.DiscussionResponse.formDiscussionEntity(discussionEntity);
                discussionResponse.setSources(discussionEntity.getSources());
                discussionResponse.setTags(discussionEntity.getTags());
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
            if (request.getParentId()!=null && discussionCommentRepository.findById(request.getParentId()).isEmpty()){
                throw new IllegalArgumentException("Parent Id is not found");
            }
            DiscussionEntity discussionEntity = discussionRepository.findById(request.getDiscussionId()).orElseThrow(()->new IllegalArgumentException("Discussion Id is not found"));
            if (request.getAuthorId()==null){
                throw new IllegalArgumentException("Author Id is required");
            }
            DiscussionCommentEntity discussionCommentEntity = modelMapperService.mapClass(request, DiscussionCommentEntity.class);
            List<DiscussionCommentEntity.SourceDto> attachments = new ArrayList<>();
            if (request.getSources() != null && !request.getSources().isEmpty()) {
                discussionCommentEntity.setAttachments(new ArrayList<>());
                for (SourceUploadDto source : request.getSources()) {
                    DiscussionCommentEntity.SourceDto sourceDto = new DiscussionCommentEntity.SourceDto();
                    sourceDto.setType(source.getType());

                    byte[] fileBytes = source.getPath().getBytes();
                    String fileName = StringUtils.generateFileName(request.getContent(), "forum");
                    CloudinaryUploadResponse response;
                    switch (source.getType()) {
                        case IMAGE:
                            byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
                            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
                            break;
                        case VIDEO:
                            String videoFileType = getFileExtension(source.getPath().getOriginalFilename());
                            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
                            break;
                        case DOCUMENT:
                            String docFileType = getFileExtension(source.getPath().getOriginalFilename());
                            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported source type");
                    }

                    sourceDto.setPath(response.getUrl());
                    sourceDto.setType(source.getType());
                    attachments.add(sourceDto);
                }
            }
            discussionCommentEntity.setAttachments(attachments);
            discussionCommentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            discussionCommentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            discussionCommentEntity.setReplyCount(0);
            discussionCommentEntity.setUpvote(0);
            discussionCommentEntity.setDownvote(0);
            discussionCommentRepository.save(discussionCommentEntity);
            if (request.getParentId()!=null && discussionCommentRepository.findById(request.getParentId()).isPresent()){
                DiscussionCommentEntity parentComment = discussionCommentRepository.findById(request.getParentId()).get();
                parentComment.setReplyCount(parentComment.getReplyCount()+1);
                discussionCommentRepository.save(parentComment);
            }
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
            List<DiscussionCommentEntity.SourceDto> attachments = new ArrayList<>();

            if (request.getSources() != null && !request.getSources().isEmpty()) {
                for (SourceUploadDto source : request.getSources()) {
                    discussionCommentEntity.setAttachments(new ArrayList<>());
                    DiscussionCommentEntity.SourceDto sourceDto = new DiscussionCommentEntity.SourceDto();
                    sourceDto.setType(source.getType());

                    byte[] fileBytes = source.getPath().getBytes();
                    String fileName = StringUtils.generateFileName(request.getContent(), "forum");
                    CloudinaryUploadResponse response;
                    switch (source.getType()) {
                        case IMAGE:
                            byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
                            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
                            break;
                        case VIDEO:
                            String videoFileType = getFileExtension(source.getPath().getOriginalFilename());
                            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
                            break;
                        case DOCUMENT:
                            String docFileType = getFileExtension(source.getPath().getOriginalFilename());
                            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported source type");
                    }

                    sourceDto.setPath(response.getUrl());
                    sourceDto.setType(source.getType());
                    attachments.add(sourceDto);
                }
            }
            discussionCommentEntity.setAttachments(attachments);
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

    @Override
    public GetDiscussionCommentResponse getReplyComments(String parentIdm, int page, int size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<DiscussionCommentEntity> discussionCommentEntities = discussionCommentRepository.findByParentId(parentIdm, pageAble);
            GetDiscussionCommentResponse getDiscussionCommentResponse = new GetDiscussionCommentResponse();
            List<GetDiscussionCommentResponse.DiscussionCommentResponse> data = new ArrayList<>();
            discussionCommentEntities.forEach(discussionCommentEntity -> {
                GetDiscussionCommentResponse.DiscussionCommentResponse discussionComment = modelMapperService.mapClass(discussionCommentEntity, GetDiscussionCommentResponse.DiscussionCommentResponse.class);
                if (String.valueOf(discussionCommentEntity.getRole())== "USER"){
                    discussionComment.setStudent(studentRepository.findById(discussionCommentEntity.getAuthorId()).get());
                }
                else{
                    discussionComment.setTeacher(teacherRepository.findById(discussionCommentEntity.getAuthorId()).get());
                }
                data.add(discussionComment);
            });
            getDiscussionCommentResponse.setComments(data);
            getDiscussionCommentResponse.setTotalElements(discussionCommentEntities.getTotalElements());
            getDiscussionCommentResponse.setTotalPage(discussionCommentEntities.getTotalPages());
            return getDiscussionCommentResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
