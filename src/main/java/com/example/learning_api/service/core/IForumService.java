package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.forum.*;
import com.example.learning_api.dto.response.forum.*;
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
    GetForumsResponse getForums(int page, int size , String search, String sortOrder, String userId,String tag,String sortBy);
    GetForumDetailResponse getForumDetail(String id, String userId);
    GetForumsResponse getForumByAuthor(String authorId, int page, int size,String search,String sortOrder,String userId,String sortBy);
    GetForumsResponse getForumByTag(List<String> tags, int page, int size,String search,String sortOrder,String userId,String sortBy);
    GetForumsResponse getForumByClass(String classId, int page, int size,String search,String sortOrder,String userId,String sortBy);
    void createForumComment(CreateForumCommentRequest request);
    void updateForumComment(UpdateForumCommentRequest request);
    void deleteForumComment(String id);
    GetForumCommentResponse getReplyComments(String parentIdm, int page, int size,String userId);
    GetForumCommentResponse getForumComments(String forumId, int page, int size, String sortOrder,String userId);

    void createTag(TagEntity request);
    void updateTag(TagEntity request);
    void deleteTag(String id);


    GetTagsResponse getTagEntity(String search, String sortOrder, int page, int size);
    GetForumsResponse getBalancedPersonalizedNewsfeed(String userId, int page, int size, String sortOrder, String sortBy);
}
