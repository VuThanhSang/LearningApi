package com.example.learning_api.dto.response.classroom;

import lombok.Data;

import java.util.List;

@Data
public class GetClassRoomRecentResponse {
    private Integer totalPage;
    private Integer totalElements;
    private List<ClassRoomResponse> classRooms;
    @Data
    public static class ClassRoomResponse {
        private String id;
        private String teacherId;
        private String classroomId;
        private String lastAccessedAt;
        private String courseId;
        private String termId;
        private String name;
        private String description;
    }

}
