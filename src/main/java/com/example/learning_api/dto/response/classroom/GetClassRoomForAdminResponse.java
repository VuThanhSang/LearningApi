package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.TestEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetClassRoomForAdminResponse {
    private ClassRoomEntity classRoom;
    private List<UserEntity> students;
    private List<Section> sections;
    private List<TestEntity> tests;
    @Data
    public static class Section {
        private String id;
        private String name;
        private String description;
        private String status;
        private int index;
        private String createdAt;
        private String updatedAt;
        private List<GetLessonDetailResponse> lessons;
    }
}
