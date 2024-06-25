package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.entity.sql.database.ScheduleEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateClassRoomResponse {
    private String id;
    private String name;
    private String description;
    private String image;
    private String courseId;
    private String teacherId;
    private String termId;
    private String facultyId;
    private Integer enrollmentCapacity;
    private Integer currentEnrollment;
    private Integer credits;
    private String status;
    private String createdAt;
    private String updatedAt;
    private List<ScheduleEntity> schedules;
}
