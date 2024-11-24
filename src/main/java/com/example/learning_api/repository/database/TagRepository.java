package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TagRepository extends MongoRepository<TagEntity, String> {
    TagEntity findByName(String name);
    @Query("{'name': {$in: ?0}}")
    List<TagEntity> findByNameIn(List<String> names);
    @Query("{'_id': {$in: ?0}}")
    List<TagEntity> findByIdIn(List<String> ids);
    @Query("{'classId': ?0}")
    TagEntity findByClassId(String classId);
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<TagEntity> findByNameRegexOrderByPostCount(String name, Pageable pageable);

}
