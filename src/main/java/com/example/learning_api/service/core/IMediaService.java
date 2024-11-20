package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.media.CreateMediaRequest;
import com.example.learning_api.dto.request.media.UpdateMediaRequest;
import com.example.learning_api.dto.response.media.GetMediaCommentsResponse;
import com.example.learning_api.dto.response.media.GetMediaNotesResponse;
import com.example.learning_api.dto.response.media.GetMediaResponse;
import com.example.learning_api.entity.sql.database.MediaCommentEntity;
import com.example.learning_api.entity.sql.database.MediaEntity;
import com.example.learning_api.entity.sql.database.MediaNoteEntity;
import com.example.learning_api.entity.sql.database.MediaProgressEntity;

public interface IMediaService {
    void createMedia(CreateMediaRequest body);
    void deleteMedia(String mediaId);
    void updateMedia(UpdateMediaRequest body);
    MediaEntity getMedia(String mediaId);
    GetMediaResponse getMediaByLessonId(String lessonId, Integer page, Integer size);
    GetMediaResponse getMediaByClassroomId(String classroomId, Integer page, Integer size);
    // student progress
    void updateMediaProgress(MediaProgressEntity body);
    MediaProgressEntity getMediaProgress(String userId, String mediaId);
    // media comment
    void createMediaComment(MediaCommentEntity body);
    void updateMediaComment(MediaCommentEntity body);
    void deleteMediaComment(String commentId);
    MediaCommentEntity getMediaComment(String commentId);
    GetMediaCommentsResponse getMediaCommentByMediaId(String mediaId, Integer page, Integer size);
    GetMediaCommentsResponse getMediaCommentByUserId(String userId, Integer page, Integer size);
    GetMediaCommentsResponse getCommentReply(String commentId, Integer page, Integer size);
    // media note
    void createMediaNote(MediaNoteEntity body);
    void updateMediaNote(MediaNoteEntity body);
    void deleteMediaNote(String noteId);
    MediaNoteEntity getMediaNote(String noteId);
    GetMediaNotesResponse getMediaNoteByMediaId(String mediaId, Integer page, Integer size);
    GetMediaNotesResponse getMediaNoteByUserId(String userId, Integer page, Integer size);
}
