package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.comment.CreateCommentRequest;
import com.example.learning_api.dto.request.comment.UpdateCommentRequest;
import com.example.learning_api.dto.response.comment.GetCommentByFaqResponse;

public interface IFaqCommentService {
    void createComment(CreateCommentRequest body);
    void updateComment(UpdateCommentRequest body);
    void deleteComment(String commentId);
    GetCommentByFaqResponse getCommentByFaqId(int page, int size, String faqId);
    GetCommentByFaqResponse getRepliesByParentId(int page, int size, String parentId);
}
