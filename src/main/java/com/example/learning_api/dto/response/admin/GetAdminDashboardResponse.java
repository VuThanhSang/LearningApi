package com.example.learning_api.dto.response.admin;

import com.example.learning_api.dto.response.classroom.TotalClassroomOfDayDto;
import com.example.learning_api.dto.common.TotalTestOfDayDto;
import lombok.Data;

import java.util.List;

@Data
public class GetAdminDashboardResponse {
    private int totalTeacher;
    private int totalStudent;
    private int totalClassroom;
    private int totalEnrollmentInMonth;
    private List<EnrollmentTrend> enrollmentTrend;
    private List<ClassroomPerformance> classroomPerfomance;
    private UserEngagement userEngagement;
    private List<String> recentActivity;
    @Data
    public static class EnrollmentTrend{
        private String date;
        private int total;
    }
    @Data
    public static class ClassroomPerformance{
        private String id;
        private String name;
        private int totalStudent;

    }
    @Data
    public static class UserEngagement{
        private int totalBlockUser;
        private int totalActiveUser;
        private int totalInactiveUser;
    }
}
