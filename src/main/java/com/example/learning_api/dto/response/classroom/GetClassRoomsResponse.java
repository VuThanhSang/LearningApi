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
        private int credit;
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
