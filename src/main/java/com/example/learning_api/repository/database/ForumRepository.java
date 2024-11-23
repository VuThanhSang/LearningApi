package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ForumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ForumRepository extends MongoRepository<ForumEntity, String> {
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    Page<ForumEntity> findAllByTitleContaining(String title, Pageable pageable);

    Page<ForumEntity> findByAuthorId(String authorId, Pageable pageable);

    // Tìm forums theo danh sách tag IDs
    @Query("{'tags': {$all: ?0}}")
    Page<ForumEntity> findByTagIds(List<String> tagIds, Pageable pageable);

    // Tìm forums có chứa ít nhất một trong các tag được chọn
    @Query("{'tags': {$in: ?0}}")
    Page<ForumEntity> findByAnyTagIds(List<String> tagIds, Pageable pageable);
}