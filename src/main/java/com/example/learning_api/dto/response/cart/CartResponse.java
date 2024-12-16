package com.example.learning_api.dto.response.cart;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import lombok.Data;

@Data
public class CartResponse {
    private String id;
    private String userId;
    private String classroomId;
    private String createdAt;
    private String updatedAt;
    private ClassRoomEntity classroom;
}
