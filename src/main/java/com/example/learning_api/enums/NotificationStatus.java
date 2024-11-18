package com.example.learning_api.enums;

public enum NotificationStatus {
    PENDING,    // Mới tạo, chưa gửi
    SENDING,    // Đang trong quá trình gửi
    SENT,       // Đã gửi thành công
    FAILED      // Gửi thất bại
}