package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.comment.CreateCommentRequest;
import com.example.learning_api.dto.request.comment.UpdateCommentRequest;
import com.example.learning_api.dto.response.comment.GetCommentByFaqResponse;
import com.example.learning_api.entity.sql.database.CommentEntity;
import com.example.learning_api.repository.database.CommentRepository;
import com.example.learning_api.repository.database.FAQRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ICommentService;
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
public class CommentService implements ICommentService {
    private final ModelMapperService modelMapperService;
    private final FAQRepository faqRepository;
    private final CommentRepository commentRepository;

    @Override
    public void createComment(CreateCommentRequest body) {
try {
            if (body.getContent() == null) {
                throw new IllegalArgumentException("Content is required");
            }
            if (body.getFaqId() == null) {
                throw new IllegalArgumentException("FaqId is required");
            }
            if (faqRepository.findById(body.getFaqId()).isEmpty()) {
                throw new IllegalArgumentException("FaqId is not found");
            }
            if(body.getParentId()!=null && body.getParentId() != "" && commentRepository.findById(body.getParentId()).isEmpty()){
                throw new IllegalArgumentException("ParentId is not found");
            }
            CommentEntity commentEntity = modelMapperService.mapClass(body, CommentEntity.class);
            commentEntity.setCreatedAt(new Date());
            commentEntity.setUpdatedAt(new Date());
            commentRepository.save(commentEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void updateComment(UpdateCommentRequest body) {
        try{
            CommentEntity commentEntity = commentRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Comment not found"));
            if (body.getContent()!=null)
                commentEntity.setContent(body.getContent());
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
            Page<CommentEntity> commentEntitiesPage = commentRepository.findByFaqId(faqId, pageable);
            List<CommentEntity> commentEntities = commentEntitiesPage.getContent();

            return GetCommentByFaqResponse.fromCommentEntities(commentEntities, commentEntitiesPage.getTotalPages(), commentEntitiesPage.getTotalElements());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
