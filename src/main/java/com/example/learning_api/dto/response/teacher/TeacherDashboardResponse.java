package com.example.learning_api.dto.response.teacher;

import lombok.Data;

import java.util.List;

@Data
public class TeacherDashboardResponse {
    private int totalClasses;
    private int totalStudents;
    private int totalRevenue;
    private Double averageRating;
    private List<RecentSales> recentSales;
    private List<Review> reviews;
    @Data
    public static class RecentSales{
        private String studentName;
        private String studentAvatar;
        private String className;
        private String classroomId;
        private int price;
    }

    @Data
    public static class Review{
        private String studentName;
        private String studentAvatar;
        private String className;
        private String classroomId;
        private Double rating;
        private String comment;
    }
}
