package com.example.learning_api.enums;

public enum NotificationFormType {
    CLASSROOM_ANNOUNCEMENT,     // Thông báo chung của khóa học
    CLASSROOM_MATERIAL_NEW,     // Tài liệu mới được thêm vào
    CLASSROOM_MATERIAL_UPDATE,  // Tài liệu được cập nhật
    CLASSROOM_SCHEDULE_CHANGE,  // Thay đổi lịch học

    // Thông báo về bài tập/kiểm tra
    DEADLINE_NEW,          // Bài tập mới
    DEADLINE_DUE_SOON,     // Bài tập sắp đến hạn
    DEADLINE_OVERDUE,      // Bài tập quá hạn
    DEADLINE_GRADED,       // Bài tập đã được chấm điểm
    TEST_NEW,               // Bài kiểm tra mới
    TEST_DUE_SOON,          // Bài kiểm tra sắp đến hạn
    TEST_GRADED,            // Bài kiểm tra đã có điểm

    // Thông báo tương tác
    FORUM_NEW_POST,     // Bài đăng mới trong diễn đàn
    FORUM_REPLY,        // Phản hồi trong diễn đàn
    FORUM_MENTION,      // Được nhắc đến trong diễn đàn


    // Thông báo về lớp học trực tuyến
    LIVE_CLASS_SCHEDULED,    // Lớp học trực tuyến mới
    LIVE_CLASS_REMINDER,     // Nhắc nhở lớp học sắp diễn ra
    LIVE_CLASS_CANCELED,     // Lớp học bị hủy
    LIVE_CLASS_RECORDING,    // Bản ghi lớp học đã có

    // Thông báo về đánh giá
    PEER_REVIEW_ASSIGNED,    // Được phân công đánh giá
    PEER_REVIEW_RECEIVED,    // Nhận được đánh giá
    INSTRUCTOR_FEEDBACK,     // Phản hồi từ giảng viên

    // Thông báo hệ thống
    SYSTEM_MAINTENANCE,      // Bảo trì hệ thống
    SYSTEM_UPDATE,          // Cập nhật hệ thống
    ACCOUNT_SECURITY,       // Bảo mật tài khoản

    // Thông báo về hoạt động cộng đồng
    GROUP_INVITATION,       // Lời mời tham gia nhóm
    GROUP_ACTIVITY,         // Hoạt động mới trong nhóm
    EVENT_INVITATION,       // Lời mời sự kiện

    // Thông báo về thanh toán/đăng ký
    PAYMENT_REMINDER,       // Nhắc nhở thanh toán
    PAYMENT_CONFIRMATION,   // Xác nhận thanh toán
    ENROLLMENT_CONFIRMATION, // Xác nhận đăng ký
    CLASSROOM_EXPIRY_SOON,      // Khóa học sắp hết hạn
    TEACHER_NOTIFICATION,
    REVIEW_CLASS,
    CLASSROOM_UPDATE,
    CLASSROOM_STATUS,

}
