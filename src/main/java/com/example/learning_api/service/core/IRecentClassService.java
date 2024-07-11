package com.example.learning_api.service.core;

import com.example.learning_api.entity.sql.database.RecentClassEntity;

import java.sql.Timestamp;
import java.util.Date;

public interface IRecentClassService {
    void createRecentClass(RecentClassEntity body);
    void updateRecentClass(String studentId, String classroomId, String lastAccessedAt);
    void deleteRecentClass(String id);

}
