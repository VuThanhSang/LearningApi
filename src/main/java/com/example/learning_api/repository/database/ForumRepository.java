package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.ForumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ForumRepository extends MongoRepository<ForumEntity, String> {
    @Query("{$or: [{'title': {$regex: ?0, $options: 'i'}}, {'content': {$regex: ?0, $options: 'i'}}]}")
    Page<ForumEntity> findByTitleOrContentRegex(String regex, Pageable pageable);

    Page<ForumEntity> findByAuthorId(String authorId, Pageable pageable);

    @Query("{$and: [{'authorId': ?0}, {$or: [{'title': {$regex: ?1, $options: 'i'}}, {'content': {$regex: ?1, $options: 'i'}}]}]}")
    Page<ForumEntity> findByAuthorIdAndTitleOrContentRegex(String authorId, String regex, Pageable pageable);

    @Query("{$and: [{'tags': {$all: ?0}}, {$or: [{'title': {$regex: ?1, $options: 'i'}}, {'content': {$regex: ?1, $options: 'i'}}]}]}")
    Page<ForumEntity> findByTagIdsAndTitleOrContentRegex(List<String> tagIds, String regex, Pageable pageable);

    @Query("{$and: [{'tags': {$in: ?0}}, {$or: [{'title': {$regex: ?1, $options: 'i'}}, {'content': {$regex: ?1, $options: 'i'}}]}]}")

    Page<ForumEntity> findByAnyTagIdsAndTitleOrContentRegex(List<String> tagIds, String regex, Pageable pageable);
    Page<ForumEntity> findByTagsIn(List<String> tagIds, Pageable pageable);
}