package com.example.learning_api.dto.response.comment;

import com.example.learning_api.entity.sql.database.FaqCommentEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GetCommentByFaqResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<CommentResponse> comments;

    @Data
    public static class CommentResponse {
        private String id;
        private String faqId;
        private String userId;
        private String content;
        private String parentId;
        private String createdAt;
        private String updatedAt;
//        private List<CommentResponse> replies; // Thêm thuộc tính để lưu trữ các comment con
        private int replies;
        public static CommentResponse formCommentEntity(FaqCommentEntity commentEntity) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setId(commentEntity.getId());
            commentResponse.setFaqId(commentEntity.getFaqId());
            commentResponse.setUserId(commentEntity.getUserId());
            commentResponse.setContent(commentEntity.getContent());
            commentResponse.setParentId(commentEntity.getParentId());
            commentResponse.setCreatedAt(commentEntity.getCreatedAt().toString());
            commentResponse.setUpdatedAt(commentEntity.getUpdatedAt().toString());
            return commentResponse;
        }
    }
}
