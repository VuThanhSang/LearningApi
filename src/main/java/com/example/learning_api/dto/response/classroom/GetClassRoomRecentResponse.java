package com.example.learning_api.dto.response.classroom;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetClassRoomRecentResponse {
    private Integer totalPage;
    private Integer totalElements;
    private List<ClassRoomResponse> classRooms;
    @Data
    public static class ClassRoomResponse {
        private String _id;
        private String name;
        private String description;
        private String image;
        private String courseId;
        private int enrollmentCapacity;
        private int currentEnrollment;
        private String status;
        private int credits;
        private String termId;
        private String lastAccessedAt;
    }

}
