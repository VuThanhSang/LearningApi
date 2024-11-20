package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.MediaCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MediaCommentRepository extends MongoRepository<MediaCommentEntity, String> {
    @Query("{ 'mediaId' : ?0 }")
    Page<MediaCommentEntity> findByMediaId(String mediaId, org.springframework.data.domain.Pageable pageable);
    @Query("{ 'userId' : ?0 }")
    Page<MediaCommentEntity> findByUserId(String userId, org.springframework.data.domain.Pageable pageable);

}
