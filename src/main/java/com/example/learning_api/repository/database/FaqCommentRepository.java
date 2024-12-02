package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.comment.GetCommentByFaqResponse;
import com.example.learning_api.entity.sql.database.FaqCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FaqCommentRepository extends MongoRepository<FaqCommentEntity, String> {


    @Aggregation(pipeline = {
            "{ $match: { faqId: ?0, parentId: null } }",
            "{ $lookup: { " +
                    "from: 'faq_comment', " +
                    "let: { commentId: { $toString: '$_id' } }, " +
                    "pipeline: [ " +
                    "{ $match: { " +
                    "$expr: { $eq: ['$parentId', '$$commentId'] }, " +
                    "faqId: ?0" +
                    "} }, " +
                    "{ $sort: { createdAt: -1 } }" +
                    "], " +
                    "as: 'replies' " +
                    "} }",
            "{ $addFields: { " +
                    "replies: { $size: '$replies' } " +
                    "} }",
            "{ $sort: { createdAt: -1 } }"
    })
    Slice<GetCommentByFaqResponse.CommentResponse> findRootCommentsByFaqIdWithReplies(String faqId, Pageable pageable);
    @Aggregation(pipeline = {
            "{ $match: { parentId: ?0 } }",
            "{ $sort: { createdAt: -1 } }"
    })
    Slice<GetCommentByFaqResponse.CommentResponse> findRepliesByParentId(String parentId, Pageable pageable);
    List<FaqCommentEntity> findByFaqId(String faqId);
}