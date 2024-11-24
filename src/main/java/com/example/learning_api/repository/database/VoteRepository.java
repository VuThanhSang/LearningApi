package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.VoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface VoteRepository extends MongoRepository<VoteEntity, String>{
    VoteEntity findByAuthorIdAndTargetId(String authorId, String forumId);
    @Query(value = "{ 'forumId' : ?0 ,isUpvote: true}", count = true)
    int countUpvoteByTargetId(String forumId);

    @Query(value = "{ 'forumId' : ?0 ,isUpvote: false}", count = true)
    int countDownvoteByTargetId(String forumId);

    @Query(value = "{ 'targetId' : ?0,'targetType': ?1}", count = true)
    List<VoteEntity> findByTargetId(String forumId, String targetType);
    @Query("{ 'targetId' : ?0, 'targetType' : ?1 , isUpvote: true}")
    List<VoteEntity> findUpVoteByTargetId(String targetId, String targetType);
    @Query("{ 'targetId' : ?0, 'targetType' : ?1 , isUpvote: false}")
    List<VoteEntity> findDownVoteByTargetId(String targetId, String targetType);

}
