package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.resource.CreateResourceRequest;
import com.example.learning_api.dto.request.resource.UpdateResourceRequest;
import com.example.learning_api.dto.response.lesson.GetResourceResponse;
import com.example.learning_api.entity.sql.database.ResourceEntity;

public interface IResourceService {
    void createResource(CreateResourceRequest body);
    void deleteResource(String resourceId);
    void updateResource(UpdateResourceRequest body);
    ResourceEntity getResource(String resourceId);
    GetResourceResponse getResourceByLessonId(String lessonId, Integer page, Integer size);
}
