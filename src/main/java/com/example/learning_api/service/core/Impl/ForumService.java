package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.request.forum.*;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.forum.GetForumCommentResponse;
import com.example.learning_api.dto.response.forum.GetForumDetailResponse;
import com.example.learning_api.dto.response.forum.GetForumsResponse;
import com.example.learning_api.entity.sql.database.FAQEntity;
import com.example.learning_api.entity.sql.database.ForumCommentEntity;
import com.example.learning_api.entity.sql.database.ForumEntity;
import com.example.learning_api.entity.sql.database.VoteEntity;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.ForumStatus;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IForumService;
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
public class ForumService implements IForumService {
    private final ForumRepository forumRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final VoteRepository voteRepository;
    @Override
    public void createForum(CreateForumRequest request) {
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
            ForumEntity forumEntity = modelMapperService.mapClass(request, ForumEntity.class);
            forumEntity.setSources(new ArrayList<>());
            processSources(request.getFiles(), request.getTitle(), forumEntity);
            forumEntity.setCommentCount(0);
            forumEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            forumEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            forumRepository.save(forumEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    public void processSources(List<MultipartFile> sources, String question, ForumEntity forumEntity) {
        if (sources.isEmpty()) {
            return;
        }

        for (MultipartFile source : sources) {
            try {
                ForumEntity.SourceDto sourceDto = processSource(source, question);
                forumEntity.getSources().add(sourceDto);
            } catch (IOException e) {
                log.error("Error processing source: " + e.getMessage());
                throw new IllegalArgumentException("Error processing source");
            }
        }
    }

    private ForumEntity.SourceDto processSource(MultipartFile file, String title) throws IOException {
        byte[] fileBytes = file.getBytes();
        String fileName = StringUtils.generateFileName(title, "forum");
        CloudinaryUploadResponse response;

        String contentType = file.getContentType();
        if (contentType.startsWith("image/")) {
            byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
        } else if (contentType.startsWith("video/")) {
            String videoFileType = getFileExtension(file.getOriginalFilename());
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
        } else if (contentType.startsWith("application/")) {
            String docFileType = getFileExtension(file.getOriginalFilename());
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
        } else {
            throw new IllegalArgumentException("Unsupported source type");
        }

        return ForumEntity.SourceDto.builder()
                .path(response.getSecureUrl())
                .type(contentType.startsWith("image/") ? FaqSourceType.IMAGE : contentType.startsWith("video/") ? FaqSourceType.VIDEO : FaqSourceType.DOCUMENT)
                .build();
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    @Override
    public void updateForum(UpdateForumRequest request) {
        try{
            ForumEntity forumEntity = forumRepository.findById(request.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (request.getTitle()!=null){
                forumEntity.setTitle(request.getTitle());
            }
            if (request.getContent()!=null){
                forumEntity.setContent(request.getContent());
            }
            if (request.getStatus()!=null){
                forumEntity.setStatus(ForumStatus.valueOf(request.getStatus()));
            }

            if (request.getSources()!=null) {
                forumEntity.getSources().clear();
                processSources(request.getSources(), request.getTitle(), forumEntity);
            }
            forumEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            forumRepository.save(forumEntity);

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteForum(String id) {
        try{
            ForumEntity forumEntity = forumRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            forumCommentRepository.deleteByForumId(id);
            forumRepository.delete(forumEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }
    @Override
    public void voteForum(VoteRequest request) {
        try {
            validateRole(request);
            ForumEntity forumEntity = forumRepository.findById(request.getForumId())
                    .orElseThrow(() -> new IllegalArgumentException("Forum Id is not found"));
            VoteEntity voteEntity = voteRepository.findByAuthorIdAndForumId(request.getAuthorId(), request.getForumId());

            if (voteEntity != null) {
                handleExistingVote(request, voteEntity);
            } else {
                createNewVote(request, forumEntity);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateRole(VoteRequest request) {
        if (request.getRole().name().equals("USER") && studentRepository.findById(request.getAuthorId()).isEmpty()) {
            throw new IllegalArgumentException("Student Id is not found");
        } else if (request.getRole().name().equals("TEACHER") && teacherRepository.findById(request.getAuthorId()).isEmpty()) {
            throw new IllegalArgumentException("Teacher Id is not found");
        }
    }

    private void handleExistingVote(VoteRequest request, VoteEntity voteEntity) {
        boolean requestUpvote = request.getIsUpvote() == 1;
        if (voteEntity.isUpvote() == requestUpvote) {
            voteRepository.delete(voteEntity);
        } else {
            voteEntity.setUpvote(requestUpvote);
            voteRepository.save(voteEntity);
        }
    }

    private void createNewVote(VoteRequest request, ForumEntity forumEntity) {
        VoteEntity voteEntity = modelMapperService.mapClass(request, VoteEntity.class);
        voteEntity.setUpvote(request.getIsUpvote() == 1);
        voteEntity.setForum(forumEntity);
        voteRepository.save(voteEntity);
    }

    @Override
    public GetForumsResponse getForums(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ForumEntity> forumEntities = forumRepository.findAllByTitleContaining(search, pageAble);
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            forumEntities.forEach(forumEntity -> {
                GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);
                forumResponse.setSources(forumEntity.getSources());
                forumResponse.setUpvote(voteRepository.countUpvoteByForumId(forumEntity.getId()));
                forumResponse.setDownvote(voteRepository.countDownvoteByForumId(forumEntity.getId()));
                data.add(forumResponse);
            });
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());
            return getForumsResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetForumDetailResponse getForumDetail(String id) {
        try{
            ForumEntity forumEntity = forumRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            GetForumDetailResponse getForumDetailResponse = modelMapperService.mapClass(forumEntity, GetForumDetailResponse.class);
            getForumDetailResponse.setUpvoteCount(voteRepository.countUpvoteByForumId(id));
            getForumDetailResponse.setDownvoteCount(voteRepository.countDownvoteByForumId(id));
            List<ForumCommentEntity> forumCommentEntities = forumCommentRepository.findByForumId(id);
            List<GetForumDetailResponse.ForumComment> forumComments = new ArrayList<>();
            forumCommentEntities.forEach(forumCommentEntity -> {
                GetForumDetailResponse.ForumComment forumComment = modelMapperService.mapClass(forumCommentEntity, GetForumDetailResponse.ForumComment.class);
                if (String.valueOf(forumCommentEntity.getRole())== "USER"){
                    forumComment.setStudent(studentRepository.findById(forumCommentEntity.getAuthorId()).get());
                }
                else{
                    forumComment.setTeacher(teacherRepository.findById(forumCommentEntity.getAuthorId()).get());
                }

                forumComments.add(forumComment);
            });
            getForumDetailResponse.setComments(forumComments);
            return getForumDetailResponse;

        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetForumsResponse getForumByAuthor(String authorId, int page, int size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ForumEntity> forumEntities = forumRepository.findByAuthorId(authorId, pageAble);
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            forumEntities.forEach(forumEntity -> {
                GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);
                forumResponse.setSources(forumEntity.getSources());
                forumResponse.setTags(forumEntity.getTags());
                forumResponse.setUpvote(voteRepository.countUpvoteByForumId(forumEntity.getId()));
                forumResponse.setDownvote(voteRepository.countDownvoteByForumId(forumEntity.getId()));
                data.add(forumResponse);
            });
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());
            return getForumsResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetForumsResponse getForumByTag(String tag, int page, int size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ForumEntity> forumEntities = forumRepository.findByTagsContaining(tag, pageAble);
            GetForumsResponse getForumsResponse = new GetForumsResponse();
            List<GetForumsResponse.ForumResponse> data = new ArrayList<>();
            forumEntities.forEach(forumEntity -> {
                GetForumsResponse.ForumResponse forumResponse = GetForumsResponse.ForumResponse.formForumEntity(forumEntity);
                forumResponse.setSources(forumEntity.getSources());
                forumResponse.setTags(forumEntity.getTags());
                forumResponse.setUpvote(voteRepository.countUpvoteByForumId(forumEntity.getId()));
                forumResponse.setDownvote(voteRepository.countDownvoteByForumId(forumEntity.getId()));

                data.add(forumResponse);
            });
            getForumsResponse.setForums(data);
            getForumsResponse.setTotalElements(forumEntities.getTotalElements());
            getForumsResponse.setTotalPage(forumEntities.getTotalPages());
            return getForumsResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void createForumComment(CreateForumCommentRequest request) {
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
            if (request.getParentId()!=null && forumCommentRepository.findById(request.getParentId()).isEmpty()){
                throw new IllegalArgumentException("Parent Id is not found");
            }
            ForumEntity forumEntity = forumRepository.findById(request.getForumId()).orElseThrow(()->new IllegalArgumentException("Forum Id is not found"));
            if (request.getAuthorId()==null){
                throw new IllegalArgumentException("Author Id is required");
            }
            ForumCommentEntity forumCommentEntity = modelMapperService.mapClass(request, ForumCommentEntity.class);
            List<ForumCommentEntity.SourceDto> attachments = new ArrayList<>();
            if (request.getSources() != null && !request.getSources().isEmpty()) {
                forumCommentEntity.setAttachments(new ArrayList<>());
                for (MultipartFile file : request.getSources()) {
                    byte[] fileBytes = file.getBytes();
                    String fileName = StringUtils.generateFileName("title", "forum");
                    CloudinaryUploadResponse response;

                    String contentType = file.getContentType();
                    if (contentType.startsWith("image/")) {
                        byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
                        response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
                    } else if (contentType.startsWith("video/")) {
                        String videoFileType = getFileExtension(file.getOriginalFilename());
                        response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
                    } else if (contentType.startsWith("application/")) {
                        String docFileType = getFileExtension(file.getOriginalFilename());
                        response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
                    } else {
                        throw new IllegalArgumentException("Unsupported source type");
                    }
                    ForumCommentEntity.SourceDto sourceDto = new ForumCommentEntity.SourceDto();
                    sourceDto.setPath(response.getUrl());
                    sourceDto.setType(contentType.startsWith("image/") ? FaqSourceType.IMAGE : contentType.startsWith("video/") ? FaqSourceType.VIDEO : FaqSourceType.DOCUMENT);
                    attachments.add(sourceDto);
                }
            }
            forumCommentEntity.setAttachments(attachments);
            forumCommentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            forumCommentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            forumCommentEntity.setReplyCount(0);
            forumCommentRepository.save(forumCommentEntity);
            if (request.getParentId()!=null && forumCommentRepository.findById(request.getParentId()).isPresent()){
                ForumCommentEntity parentComment = forumCommentRepository.findById(request.getParentId()).get();
                parentComment.setReplyCount(parentComment.getReplyCount()+1);
                forumCommentRepository.save(parentComment);
            }
            forumEntity.setCommentCount(forumEntity.getCommentCount()+1);
            forumRepository.save(forumEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateForumComment(UpdateForumCommentRequest request) {
        try {
            ForumCommentEntity forumCommentEntity = forumCommentRepository.findById(request.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (request.getContent()!=null){
                forumCommentEntity.setContent(request.getContent());
            }
            if (request.getReplyCount()!=0){
                forumCommentEntity.setReplyCount(request.getReplyCount());
            }
            if (request.getStatus()!=null){
                forumCommentEntity.setStatus(ForumStatus.valueOf(request.getStatus()));
            }
            List<ForumCommentEntity.SourceDto> attachments = new ArrayList<>();

            if (request.getSources() != null && !request.getSources().isEmpty()) {
                for (MultipartFile file : request.getSources()) {
                    byte[] fileBytes = file.getBytes();
                    String fileName = StringUtils.generateFileName("title", "forum");
                    CloudinaryUploadResponse response;

                    String contentType = file.getContentType();
                    if (contentType.startsWith("image/")) {
                        byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
                        response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
                    } else if (contentType.startsWith("video/")) {
                        String videoFileType = getFileExtension(file.getOriginalFilename());
                        response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
                    } else if (contentType.startsWith("application/")) {
                        String docFileType = getFileExtension(file.getOriginalFilename());
                        response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
                    } else {
                        throw new IllegalArgumentException("Unsupported source type");
                    }
                    ForumCommentEntity.SourceDto sourceDto = new ForumCommentEntity.SourceDto();
                    sourceDto.setPath(response.getUrl());
                    sourceDto.setType(contentType.startsWith("image/") ? FaqSourceType.IMAGE : contentType.startsWith("video/") ? FaqSourceType.VIDEO : FaqSourceType.DOCUMENT);
                    attachments.add(sourceDto);
                }
            }
            forumCommentEntity.setAttachments(attachments);
            forumCommentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            forumCommentRepository.save(forumCommentEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteForumComment(String id) {
        try{
            ForumCommentEntity forumCommentEntity = forumCommentRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            forumCommentRepository.delete(forumCommentEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetForumCommentResponse getReplyComments(String parentIdm, int page, int size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ForumCommentEntity> forumCommentEntities = forumCommentRepository.findByParentId(parentIdm, pageAble);
            GetForumCommentResponse getForumCommentResponse = new GetForumCommentResponse();
            List<GetForumCommentResponse.ForumCommentResponse> data = new ArrayList<>();
            forumCommentEntities.forEach(forumCommentEntity -> {
                GetForumCommentResponse.ForumCommentResponse forumComment = modelMapperService.mapClass(forumCommentEntity, GetForumCommentResponse.ForumCommentResponse.class);
                if (String.valueOf(forumCommentEntity.getRole())== "USER"){
                    forumComment.setStudent(studentRepository.findById(forumCommentEntity.getAuthorId()).get());
                }
                else{
                    forumComment.setTeacher(teacherRepository.findById(forumCommentEntity.getAuthorId()).get());
                }
                data.add(forumComment);
            });
            getForumCommentResponse.setComments(data);
            getForumCommentResponse.setTotalElements(forumCommentEntities.getTotalElements());
            getForumCommentResponse.setTotalPage(forumCommentEntities.getTotalPages());
            return getForumCommentResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
