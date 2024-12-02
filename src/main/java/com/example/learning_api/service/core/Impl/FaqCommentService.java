package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.request.comment.CreateCommentRequest;
import com.example.learning_api.dto.request.comment.UpdateCommentRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.comment.GetCommentByFaqResponse;
import com.example.learning_api.entity.sql.database.FAQEntity;
import com.example.learning_api.entity.sql.database.FaqCommentEntity;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IFaqCommentService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqCommentService implements IFaqCommentService {
    private final ModelMapperService modelMapperService;
    private final FAQRepository faqRepository;
    private final FaqCommentRepository commentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CloudinaryService cloudinaryService;
    private final FileRepository fileRepository;
    public void processFiles (List<MultipartFile> files, String title, FaqCommentEntity deadlineEntity){
        if (files == null) {
            return;
        }
        for (MultipartFile file : files) {
            try {
                FAQEntity.SourceDto fileDto = processFile(file, title);
                FileEntity fileEntity = new FileEntity();
                fileEntity.setUrl(fileDto.getPath());
                fileEntity.setType(fileDto.getType().name());
                fileEntity.setOwnerType(FileOwnerType.FAQ_COMMENT);
                fileEntity.setOwnerId(deadlineEntity.getId());
                fileEntity.setExtension(fileDto.getPath().substring(fileDto.getPath().lastIndexOf(".") + 1));
                fileEntity.setName(file.getOriginalFilename());
                fileEntity.setSize(String.valueOf(file.getSize()));
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileRepository.save(fileEntity);
            } catch (IOException e) {
                log.error("Error processing file: ", e);
                throw new IllegalArgumentException("Error processing file: " + e.getMessage());
            }
        }
    }

    public FAQEntity.SourceDto processFile(MultipartFile file, String title) throws IOException {
        byte[] fileBytes = file.getBytes();
        String fileName = StringUtils.generateFileName(file.getOriginalFilename(), "deadline");
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
        }  else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + ".docx", fileBytes, "raw");
        }
        else {
            throw new IllegalArgumentException("Unsupported source type");
        }

        return FAQEntity.SourceDto.builder()
                .path(response.getSecureUrl())
                .type(contentType.startsWith("image/") ? FaqSourceType.IMAGE : contentType.startsWith("video/") ? FaqSourceType.VIDEO : FaqSourceType.DOCUMENT)
                .build();
    }
    @Override
    public void createComment(CreateCommentRequest body) {
        try {
            validateRequest(body);
            FaqCommentEntity commentEntity = modelMapperService.mapClass(body, FaqCommentEntity.class);

            commentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            commentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            commentRepository.save(commentEntity);
                processFiles(body.getSources(),body.getContent(), commentEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    public void validateRequest(CreateCommentRequest body) {
        if (body.getFaqId() == null) {
            throw new IllegalArgumentException("FaqId is required");
        }
        if (faqRepository.findById(body.getFaqId()).isEmpty()) {
            throw new IllegalArgumentException("FaqId is not found");
        }

    }


    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }


    @Override
    public void updateComment(UpdateCommentRequest body) {
        try{
            FaqCommentEntity commentEntity = commentRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Comment not found"));
            if (body.getContent()!=null)
                commentEntity.setContent(body.getContent());
            if (body.getSources()!=null ){
                if ( body.getSources().size()>0){
//                        commentEntity.setSources(new ArrayList<>());
                    processFiles(body.getSources(),body.getContent(), commentEntity);
                }
            }
            commentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            commentRepository.save(commentEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void deleteComment(String commentId) {
        try{
            commentRepository.deleteById(commentId);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetCommentByFaqResponse getCommentByFaqId(int page, int size, String faqId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Slice<GetCommentByFaqResponse.CommentResponse> commentEntitiesPage = commentRepository.findRootCommentsByFaqIdWithReplies(faqId, pageable);
            List<GetCommentByFaqResponse.CommentResponse> commentEntities = commentEntitiesPage.getContent();
            GetCommentByFaqResponse response = new GetCommentByFaqResponse();
            List<GetCommentByFaqResponse.CommentResponse> commentResponses = new ArrayList<>();
            for (GetCommentByFaqResponse.CommentResponse commentEntity : commentEntities) {
                GetCommentByFaqResponse.CommentResponse commentResponse = new GetCommentByFaqResponse.CommentResponse();
                commentResponse.setId(commentEntity.getId());
                commentResponse.setFaqId(commentEntity.getFaqId());
                commentResponse.setUserId(commentEntity.getUserId());
                commentResponse.setContent(commentEntity.getContent());
                commentResponse.setCreatedAt(commentEntity.getCreatedAt().toString());
                commentResponse.setUpdatedAt(commentEntity.getUpdatedAt().toString());
                commentResponse.setReplies(commentEntity.getReplies());
                commentResponses.add(commentResponse);
            }
            response.setComments(commentResponses);
            response.setTotalElements((long) commentEntitiesPage.getNumberOfElements());
            response.setTotalPage(
                    (int) Math.ceil((double) commentEntitiesPage.getNumberOfElements() / commentEntitiesPage.getSize())
            );
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetCommentByFaqResponse getRepliesByParentId(int page, int size, String parentId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Slice<GetCommentByFaqResponse.CommentResponse> commentEntitiesPage = commentRepository.findRepliesByParentId(parentId, pageable);
            List<GetCommentByFaqResponse.CommentResponse> commentEntities = commentEntitiesPage.getContent();
            GetCommentByFaqResponse response = new GetCommentByFaqResponse();
            List<GetCommentByFaqResponse.CommentResponse> commentResponses = new ArrayList<>();
            for (GetCommentByFaqResponse.CommentResponse commentEntity : commentEntities) {
                GetCommentByFaqResponse.CommentResponse commentResponse = new GetCommentByFaqResponse.CommentResponse();
                commentResponse.setId(commentEntity.getId());
                commentResponse.setFaqId(commentEntity.getFaqId());
                commentResponse.setUserId(commentEntity.getUserId());
                commentResponse.setContent(commentEntity.getContent());
                commentResponse.setCreatedAt(commentEntity.getCreatedAt().toString());
                commentResponse.setUpdatedAt(commentEntity.getUpdatedAt().toString());
                commentResponse.setReplies(commentEntity.getReplies());
                commentResponses.add(commentResponse);
            }
            response.setComments(commentResponses);
            response.setTotalElements((long) commentEntitiesPage.getNumberOfElements());
            response.setTotalPage(
                    (int) Math.ceil((double) commentEntitiesPage.getNumberOfElements() / commentEntitiesPage.getSize())
            );
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
