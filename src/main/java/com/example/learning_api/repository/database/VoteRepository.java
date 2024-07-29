package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.VoteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface VoteRepository extends MongoRepository<VoteEntity, String>{
    VoteEntity findByAuthorIdAndForumId(String authorId, String forumId);
    @Query(value = "{ 'forumId' : ?0 ,isUpvote: true}", count = true)
    int countUpvoteByForumId(String forumId);

    @Query(value = "{ 'forumId' : ?0 ,isUpvote: false}", count = true)
    int countDownvoteByForumId(String forumId);

}
