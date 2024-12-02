package com.example.learning_api.dto.response.admin;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetClassRoomsAdminResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<ClassRoomEntity> data;
}

