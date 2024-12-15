package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.progress.ProgressCompleteRequest;
import com.example.learning_api.entity.sql.database.ProgressEntity;

public interface IProgressService {
    void markLessonAsCompleted(ProgressCompleteRequest body);
    void markSectionAsCompleted(ProgressCompleteRequest body);
}
