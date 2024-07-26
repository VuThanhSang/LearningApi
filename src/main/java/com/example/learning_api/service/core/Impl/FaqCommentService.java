package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.request.comment.CreateCommentRequest;
import com.example.learning_api.dto.request.comment.UpdateCommentRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.comment.GetCommentByFaqResponse;
import com.example.learning_api.entity.sql.database.FAQEntity;
import com.example.learning_api.entity.sql.database.FaqCommentEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.repository.database.FAQRepository;
import com.example.learning_api.repository.database.FaqCommentRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
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
    @Override
    public void createComment(CreateCommentRequest body) {
        try {
            validateRequest(body);
            FaqCommentEntity commentEntity = modelMapperService.mapClass(body, FaqCommentEntity.class);
            if (body.getSources()!=null && body.getSources().size()>0){
                commentEntity.setSources(new ArrayList<>());
                processSources(body.getSources(),body.getContent(), commentEntity);
            }

            commentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            commentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            commentRepository.save(commentEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    public void validateRequest(CreateCommentRequest body) {
        if (body.getContent() == null) {
            throw new IllegalArgumentException("Content is required");
        }
        if (body.getFaqId() == null) {
            throw new IllegalArgumentException("FaqId is required");
        }
        if (faqRepository.findById(body.getFaqId()).isEmpty()) {
            throw new IllegalArgumentException("FaqId is not found");
        }
        if (body.getRole()== RoleEnum.USER){
            if (body.getUserId() == null) {
                throw new IllegalArgumentException("UserId is required");
            }
            if (studentRepository.findById(body.getUserId()).isEmpty()) {
                throw new IllegalArgumentException("UserId is not found");
            }
        }
        else {
            if (body.getUserId() == null) {
                throw new IllegalArgumentException("UserId is required");
            }
            if (teacherRepository.findById(body.getUserId()).isEmpty()) {
                throw new IllegalArgumentException("UserId is not found");
            }
        }
        if (body.getParentId() != null && body.getParentId() != "" && commentRepository.findById(body.getParentId()).isEmpty()) {
            throw new IllegalArgumentException("ParentId is not found");
        }
    }


    public void processSources(List<SourceUploadDto> sources, String question, FaqCommentEntity faqCommentEntity) {
        if (sources.isEmpty()) {
            return;
        }

        for (SourceUploadDto source : sources) {
            try {
                FaqCommentEntity.SourceDto sourceDto = processSource(source, question);
                faqCommentEntity.getSources().add(sourceDto);
            } catch (IOException e) {
                log.error("Error processing source: " + e.getMessage());
                throw new IllegalArgumentException("Error processing source");
            }
        }
    }

    private FaqCommentEntity.SourceDto processSource(SourceUploadDto source, String question) throws IOException {
        FaqCommentEntity.SourceDto sourceDto = new FaqCommentEntity.SourceDto();
        sourceDto.setType(source.getType());

        byte[] fileBytes = source.getPath().getBytes();
        String fileName = StringUtils.generateFileName(question, "FaqComment");

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
    public void updateComment(UpdateCommentRequest body) {
        try{
            FaqCommentEntity commentEntity = commentRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Comment not found"));
            if (body.getContent()!=null)
                commentEntity.setContent(body.getContent());
            if (commentEntity.getSources()!=null){
                if (body.getSources()!=null && body.getSources().size()>0){
                    commentEntity.getSources().clear();
                    processSources(body.getSources(),body.getContent(), commentEntity);
                }
            }
            else{
                if (body.getSources()!=null ){
                    if ( body.getSources().size()>0){
                        commentEntity.setSources(new ArrayList<>());
                        processSources(body.getSources(),body.getContent(), commentEntity);
                    }
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
                commentResponse.setParentId(commentEntity.getParentId());
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
                commentResponse.setParentId(commentEntity.getParentId());
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
