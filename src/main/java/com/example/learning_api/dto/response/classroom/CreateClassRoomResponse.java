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
    private String teacherId;
    private Integer enrollmentCapacity;
    private Integer currentEnrollment;
    private Integer price;
    private Integer totalLesson;
    private Integer totalStudent;
    private Integer totalResource;
    private Integer totalAssignment;
    private Integer totalQuiz;
    private Integer totalExam;
    private String categoryId;
    private String status;
    private String createdAt;
    private String updatedAt;
}
