package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ForumCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ForumCommentRepository extends MongoRepository<ForumCommentEntity, String>{
    List<ForumCommentEntity> findByForumId(String forumId);
    Page<ForumCommentEntity> findByForumId(String forumId, Pageable pageable);
    Page<ForumCommentEntity> findByParentId(String parentId, Pageable pageable);
    void deleteByForumId(String forumId);
}
