package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.resource.CreateResourceRequest;
import com.example.learning_api.dto.request.resource.UpdateResourceRequest;

public interface IResourceService {
    void createResource(CreateResourceRequest body);
    void deleteResource(String resourceId);
    void updateResource(UpdateResourceRequest body);
}
