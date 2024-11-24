package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.forum.*;
import com.example.learning_api.dto.response.forum.GetForumCommentResponse;
import com.example.learning_api.dto.response.forum.GetForumDetailResponse;
import com.example.learning_api.dto.response.forum.GetForumsResponse;
import com.example.learning_api.dto.response.forum.GetVotesResponse;
import com.example.learning_api.entity.sql.database.TagEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IForumService {
    void createForum(CreateForumRequest request);
    void updateForum(UpdateForumRequest request);
    void deleteForum(String id);
    void voteForum(VoteRequest request);
    GetVotesResponse getVotedByForum(String forumId);
    GetVotesResponse getVoteByComment(String commentId);
    GetForumsResponse getForums(int page, int size , String search, String sortOrder, String userId);
    GetForumDetailResponse getForumDetail(String id, String userId);
    GetForumsResponse getForumByAuthor(String authorId, int page, int size,String search,String sortOrder,String userId);
    GetForumsResponse getForumByTag(List<String> tags, int page, int size,String search,String sortOrder,String userId);
    GetForumsResponse getForumByClass(String classId, int page, int size,String search,String sortOrder,String userId);
    void createForumComment(CreateForumCommentRequest request);
    void updateForumComment(UpdateForumCommentRequest request);
    void deleteForumComment(String id);
    GetForumCommentResponse getReplyComments(String parentIdm, int page, int size,String userId);
    GetForumCommentResponse getForumComments(String forumId, int page, int size, String sortOrder,String userId);

    void createTag(TagEntity request);
    void updateTag(TagEntity request);
    void deleteTag(String id);

}
