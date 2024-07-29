package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ForumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ForumRepository extends MongoRepository<ForumEntity, String> {
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    Page<ForumEntity> findAllByTitleContaining(String title, org.springframework.data.domain.Pageable pageable);
    Page<ForumEntity> findByAuthorId(String authorId, org.springframework.data.domain.Pageable pageable);
    @Query("{'tags': {$regex: ?0, $options: 'i'}}")
    Page<ForumEntity> findByTagsContaining(String tag, org.springframework.data.domain.Pageable pageable);

}
