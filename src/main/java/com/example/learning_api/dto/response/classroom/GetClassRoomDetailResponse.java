package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.enums.ClassRoomStatus;
import lombok.Data;

import java.util.List;

@Data
public class GetClassRoomDetailResponse {
    private String id;
    private String name;
    private String description;
    private String image;
    private Integer enrollmentCapacity;
    private Integer currentEnrollment;
    private String inviteCode;
    private ClassRoomStatus status;
    private Integer credits;
    private String teacherId;
    private String createdAt;
    private String updatedAt;
    private List<Section> sections;
    @Data
    public static class Section {
        private String id;
        private String name;
        private String description;
        private String status;
        private int index;
        private Boolean canAccess;
        private Boolean isComplete;
        private String createdAt;
        private String updatedAt;
//        private List<GetLessonDetailResponse> lessons;
    }
}
