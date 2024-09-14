package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.WorkspaceMemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkspaceMemberRepository extends MongoRepository<WorkspaceMemberEntity, String> {
    WorkspaceMemberEntity findByWorkspaceIdAndMemberId(String workspaceId, String memberId);
    void deleteByWorkspaceIdAndMemberId(String workspaceId, String memberId);

}
