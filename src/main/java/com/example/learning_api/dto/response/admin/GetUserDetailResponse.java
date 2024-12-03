package com.example.learning_api.dto.response.admin;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetUserDetailResponse {
    private UserEntity user;
    private List<ClassRoomEntity> classRooms;

}
