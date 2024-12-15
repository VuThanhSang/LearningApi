package com.example.learning_api.dto.response.classroom;


import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.enums.ClassRoomStatus;
import lombok.Data;

import java.util.List;

@Data
public class GetClassRoomsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<ClassRoomResponse> classRooms;

    public GetClassRoomsResponse(List<ClassRoomResponse> resData, int totalPages, long totalElements) {
        this.classRooms = resData;
        this.totalPage = totalPages;
        this.totalElements = totalElements;
    }

    public GetClassRoomsResponse() {

    }

    @Data
    public static class ClassRoomResponse {
        private String id;
        private String name;
        private String description;
        private String teacherId;
        private String courseId;
        private String image;
        private int enrollmentCapacity;
        private int currentEnrollment;
        private String status;
        private Long duration;
        private Integer totalVideo;
        private Integer totalLesson;
        private Integer totalStudent;
        private Integer totalResource;
        private Integer totalAssignment;
        private Integer totalQuiz;
        private Integer totalExam;
        private Integer totalDocument;
        private Integer price;
        private List<String> categories;
        private String createdAt;
        private String updatedAt;
        public static ClassRoomResponse formClassRoomEntity(ClassRoomEntity classRoomEntity){
            ClassRoomResponse classRoomResponse = new ClassRoomResponse();
            classRoomResponse.setId(classRoomEntity.getId());
            classRoomResponse.setName(classRoomEntity.getName());
            classRoomResponse.setDescription(classRoomEntity.getDescription());
            classRoomResponse.setCreatedAt(classRoomEntity.getCreatedAt().toString());
            classRoomResponse.setUpdatedAt(classRoomEntity.getUpdatedAt().toString());
            classRoomResponse.setTeacherId(classRoomEntity.getTeacherId());
            classRoomResponse.setImage(classRoomEntity.getImage());
            classRoomResponse.setEnrollmentCapacity(classRoomEntity.getEnrollmentCapacity());
            classRoomResponse.setCurrentEnrollment(classRoomEntity.getCurrentEnrollment());
            classRoomResponse.setStatus(classRoomEntity.getStatus().name());
            return classRoomResponse;
        }
    }



}
