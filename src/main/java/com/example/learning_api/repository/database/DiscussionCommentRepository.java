package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DiscussionCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DiscussionCommentRepository extends MongoRepository<DiscussionCommentEntity, String>{
    List<DiscussionCommentEntity> findByDiscussionId(String discussionId);
    Page<DiscussionCommentEntity> findByParentId(String parentId, Pageable pageable);
    void deleteByDiscussionId(String discussionId);
}
