package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.media.CreateMediaRequest;
import com.example.learning_api.dto.request.media.UpdateMediaRequest;
import com.example.learning_api.dto.response.lesson.GetMediaResponse;
import com.example.learning_api.entity.sql.database.MediaEntity;

public interface IMediaService {
    void createMedia(CreateMediaRequest body);
    void deleteMedia(String mediaId);
    void updateMedia(UpdateMediaRequest body);
    MediaEntity getMedia(String mediaId);
    GetMediaResponse getMediaByLessonId(String lessonId, Integer page, Integer size);
}
