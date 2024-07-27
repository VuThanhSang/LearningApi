package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DiscussionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DiscussionRepository extends MongoRepository<DiscussionEntity, String> {
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    Page<DiscussionEntity> findAllByTitleContaining(String title, org.springframework.data.domain.Pageable pageable);
    Page<DiscussionEntity> findByAuthorId(String authorId, org.springframework.data.domain.Pageable pageable);
    @Query("{'tags': {$regex: ?0, $options: 'i'}}")
    Page<DiscussionEntity> findByTagsContaining(String tag, org.springframework.data.domain.Pageable pageable);

}
