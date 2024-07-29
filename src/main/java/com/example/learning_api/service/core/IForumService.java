package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.forum.*;
import com.example.learning_api.dto.response.forum.GetForumCommentResponse;
import com.example.learning_api.dto.response.forum.GetForumDetailResponse;
import com.example.learning_api.dto.response.forum.GetForumsResponse;

public interface IForumService {
    void createForum(CreateForumRequest request);
    void updateForum(UpdateForumRequest request);
    void deleteForum(String id);
    void voteForum(VoteRequest request);
    GetForumsResponse getForums(int page, int size , String search);
    GetForumDetailResponse getForumDetail(String id);
    GetForumsResponse getForumByAuthor(String authorId, int page, int size);
    GetForumsResponse getForumByTag(String tag, int page, int size);

    void createForumComment(CreateForumCommentRequest request);
    void updateForumComment(UpdateForumCommentRequest request);
    void deleteForumComment(String id);
    GetForumCommentResponse getReplyComments(String parentIdm, int page, int size);


}
