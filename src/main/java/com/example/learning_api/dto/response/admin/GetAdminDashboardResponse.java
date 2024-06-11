package com.example.learning_api.dto.response.admin;

import com.example.learning_api.dto.common.TotalClassroomOfDayDto;
import com.example.learning_api.dto.common.TotalTestOfDayDto;
import lombok.Data;

import java.util.List;

@Data
public class GetAdminDashboardResponse {
    private int totalTeacher;
    private int totalStudent;
    private int totalCourse;
    private ClassroomAndTest scheduleAndTest;
    @Data
    public static class ClassroomAndTest{
        private  List<TotalClassroomOfDayDto> totalClassroom;
        private List<TotalTestOfDayDto> totalTest;
    }

}
