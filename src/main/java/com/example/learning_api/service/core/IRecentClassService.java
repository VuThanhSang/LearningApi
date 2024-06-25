package com.example.learning_api.service.core;

import com.example.learning_api.entity.sql.database.RecentClassEntity;

import java.util.Date;

public interface IRecentClassService {
    void createRecentClass(RecentClassEntity body);
    void updateRecentClass(String studentId, String classroomId, Date lastAccessedAt);
    void deleteRecentClass(String id);

}
