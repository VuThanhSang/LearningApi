package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.discussion.CreateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.CreateDiscussionRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionRequest;
import com.example.learning_api.dto.response.discussion.GetDiscussionCommentResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionDetailResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionsResponse;
import com.example.learning_api.entity.sql.database.DiscussionCommentEntity;

import java.util.List;

public interface IDiscussionService {
    void createDiscussion(CreateDiscussionRequest request);
    void updateDiscussion(UpdateDiscussionRequest request);
    void deleteDiscussion(String id);
    void upvoteDiscussion(String id);
    void downvoteDiscussion(String id);
    GetDiscussionsResponse getDiscussions(int page, int size , String search);
    GetDiscussionDetailResponse getDiscussionDetail(String id);
    GetDiscussionsResponse getDiscussionByAuthor(String authorId, int page, int size);
    GetDiscussionsResponse getDiscussionByTag(String tag, int page, int size);

    void createDiscussionComment(CreateDiscussionCommentRequest request);
    void updateDiscussionComment(UpdateDiscussionCommentRequest request);
    void deleteDiscussionComment(String id);
    GetDiscussionCommentResponse getReplyComments(String parentIdm, int page, int size);


}
