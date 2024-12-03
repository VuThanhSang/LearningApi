package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetClassRoomRecentResponse {
    private Integer totalPage;
    private Integer totalElements;
    private List<ClassRoomResponse> data;
    @Data
    public static class ClassRoomResponse {
        private ClassRoomEntity classRoom;
        private String lastAccessedAt;
    }

}
