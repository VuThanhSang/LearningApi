package com.example.learning_api.module.vnpay;

import lombok.Builder;

import java.util.List;

public abstract class PaymentDTO {
    @Builder
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;
    }
}