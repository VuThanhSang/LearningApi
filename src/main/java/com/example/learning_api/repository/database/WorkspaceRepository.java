package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.WorkspaceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkspaceRepository extends MongoRepository<WorkspaceEntity, String>{
}
