package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.CategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CategoryRepository extends MongoRepository<CategoryEntity, String> {
    @Query("{ 'name' : { $regex: ?0, $options: 'i' } }")
    List<CategoryEntity> findAllSortByTotalClassRoomDescAndNameContaining(String name);


}
