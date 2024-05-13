package com.example.learning_api.dto.response.comment;

import com.example.learning_api.entity.sql.database.CommentEntity;
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

    public static GetCommentByFaqResponse fromCommentEntities(List<CommentEntity> commentEntities, int totalPage, long totalElements) {
        GetCommentByFaqResponse response = new GetCommentByFaqResponse();
        response.setTotalPage(totalPage);
        response.setTotalElements(totalElements);

        Map<String, CommentResponse> commentMap = new HashMap<>();
        List<CommentResponse> rootComments = new ArrayList<>();

        for (CommentEntity commentEntity : commentEntities) {
            CommentResponse commentResponse = CommentResponse.formCommentEntity(commentEntity);
            commentMap.put(commentResponse.getId(), commentResponse);

            if (commentResponse.getParentId() == null || commentResponse.getParentId().isEmpty()) {
                rootComments.add(commentResponse);
            } else {
                CommentResponse parentComment = commentMap.get(commentResponse.getParentId());
                if (parentComment != null) {
                    parentComment.getReplies().add(commentResponse);
                }
            }
        }

        response.setComments(rootComments);
        return response;
    }
    @Data
    public static class CommentResponse {
        private String id;
        private String faqId;
        private String userId;
        private String content;
        private String parentId;
        private String createdAt;
        private String updatedAt;
        private List<CommentResponse> replies; // Thêm thuộc tính để lưu trữ các comment con

        public static CommentResponse formCommentEntity(CommentEntity commentEntity) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setId(commentEntity.getId());
            commentResponse.setFaqId(commentEntity.getFaqId());
            commentResponse.setUserId(commentEntity.getUserId());
            commentResponse.setContent(commentEntity.getContent());
            commentResponse.setParentId(commentEntity.getParentId());
            commentResponse.setCreatedAt(commentEntity.getCreatedAt().toString());
            commentResponse.setUpdatedAt(commentEntity.getUpdatedAt().toString());
            commentResponse.setReplies(new ArrayList<>()); // Khởi tạo danh sách replies rỗng
            return commentResponse;
        }
    }
}
