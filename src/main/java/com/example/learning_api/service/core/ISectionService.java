package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.section.CreateSectionRequest;
import com.example.learning_api.dto.request.section.DeleteSectionRequest;
import com.example.learning_api.dto.request.section.UpdateSectionRequest;
import com.example.learning_api.dto.response.section.CreateSectionResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;

public interface ISectionService {
    CreateSectionResponse createSection(CreateSectionRequest body);
    void updateSection(UpdateSectionRequest body);
    void deleteSection(DeleteSectionRequest id);
    GetSectionsResponse getSections(int page, int size, String search, String role);
    GetSectionsResponse getSectionsByClassRoomId(String classRoomId, int page, int size,String role);
}
