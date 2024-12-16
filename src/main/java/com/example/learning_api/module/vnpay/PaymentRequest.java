package com.example.learning_api.module.vnpay;

import lombok.Data;

import java.util.List;

@Data
public class PaymentRequest {
    private String userId;
    private List<String> classroomIds;
}
