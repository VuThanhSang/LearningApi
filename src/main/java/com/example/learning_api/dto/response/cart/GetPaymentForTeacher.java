package com.example.learning_api.dto.response.cart;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetPaymentForTeacher {
    private Long totalPrice;
    private Long totalClassroom;
    private Long totalElement;
    private int totalPage;
    private List<Transaction> transactions;
    @Data
    public static class Transaction{
        private String transactionId;
        private Long amount;
        private String transactionRef;
        private UserEntity user;
        private String status;
        private ClassRoomEntity classroom;
        private String createdAt;
        private String updatedAt;
    }
}
