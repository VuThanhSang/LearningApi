package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.MediaNoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

public  interface MediaNoteRepository extends MongoRepository<MediaNoteEntity, String> {
    MediaNoteEntity findByUserIdAndMediaId(String userId, String mediaId);
    Page<MediaNoteEntity> findByUserId(String userId, org.springframework.data.domain.Pageable pageable);
    Page<MediaNoteEntity> findByMediaId(String mediaId, org.springframework.data.domain.Pageable pageable);
}
