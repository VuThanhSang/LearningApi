package com.example.learning_api.dto.response.ClassRoomResponse;


import com.example.learning_api.entity.sql.database.ClassRoomEntity;
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
        private String createdAt;
        private String updatedAt;
        public static ClassRoomResponse formClassRoomEntity(ClassRoomEntity classRoomEntity){
            ClassRoomResponse classRoomResponse = new ClassRoomResponse();
            classRoomResponse.setId(classRoomEntity.getId());
            classRoomResponse.setName(classRoomEntity.getName());
            classRoomResponse.setDescription(classRoomEntity.getDescription());
            classRoomResponse.setTeacherId(classRoomEntity.getTeacherId());
            classRoomResponse.setCreatedAt(classRoomEntity.getCreatedAt().toString());
            classRoomResponse.setUpdatedAt(classRoomEntity.getUpdatedAt().toString());
            return classRoomResponse;
        }
    }

}
