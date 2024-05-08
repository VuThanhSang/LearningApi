package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.media.CreateMediaRequest;
import com.example.learning_api.dto.request.media.UpdateMediaRequest;

public interface IMediaService {
    void createMedia(CreateMediaRequest body);
    void deleteMedia(String mediaId);
    void updateMedia(UpdateMediaRequest body);
}
