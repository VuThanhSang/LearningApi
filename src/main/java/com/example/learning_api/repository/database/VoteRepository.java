package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.VoteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface VoteRepository extends MongoRepository<VoteEntity, String>{
    VoteEntity findByAuthorIdAndDiscussionId(String authorId, String discussionId);
    @Query(value = "{ 'discussionId' : ?0 ,isUpvote: true}", count = true)
    int countUpvoteByDiscussionId(String discussionId);

    @Query(value = "{ 'discussionId' : ?0 ,isUpvote: false}", count = true)
    int countDownvoteByDiscussionId(String discussionId);

}
