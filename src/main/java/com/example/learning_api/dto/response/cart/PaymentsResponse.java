package com.example.learning_api.dto.response.cart;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import lombok.Data;

import java.util.List;

@Data
public class PaymentsResponse {
    private Long amountPaid;
    private String paymentMethod;
    private List<ClassRoomEntity> classRooms;
    private String transactionRef;
    private String date;
}
