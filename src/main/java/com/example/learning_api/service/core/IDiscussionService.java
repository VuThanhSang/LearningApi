package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.discussion.CreateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.CreateDiscussionRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionRequest;
import com.example.learning_api.dto.response.discussion.GetDiscussionDetailResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionsResponse;

public interface IDiscussionService {
    void createDiscussion(CreateDiscussionRequest request);
    void updateDiscussion(UpdateDiscussionRequest request);
    void deleteDiscussion(String id);
    GetDiscussionsResponse getDiscussions(int page, int size , String search);
    GetDiscussionDetailResponse getDiscussionDetail(String id);
    void createDiscussionComment(CreateDiscussionCommentRequest request);
    void updateDiscussionComment(UpdateDiscussionCommentRequest request);
    void deleteDiscussionComment(String id);


}
