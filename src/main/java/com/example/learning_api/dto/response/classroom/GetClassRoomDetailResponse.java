package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.enums.ClassRoomStatus;
import lombok.Data;

import java.util.List;

@Data
public class GetClassRoomDetailResponse {
    private String id;
    private String name;
    private String description;
    private String image;
    private Integer enrollmentCapacity;
    private Integer currentEnrollment;
    private String inviteCode;
    private ClassRoomStatus status;
    private Integer credits;
    private String teacherId;
    private String createdAt;
    private String updatedAt;
    private Long duration;
    private Integer totalVideo;
    private Integer totalLesson;
    private Integer totalStudent;
    private Integer totalResource;
    private Integer totalAssignment;
    private Integer totalQuiz;
    private Integer totalExam;
    private Integer totalDocument;
    private Integer price;
    private List<String> categories;
    private Boolean isPublic;
    private boolean isEnrolled;
    private UserEntity user;
    private List<Section> sections;
    @Data
    public static class Section {
        private String id;
        private String name;
        private String description;
        private String status;
        private int index;
        private Boolean canAccess;
        private Boolean isComplete;
        private String createdAt;
        private String updatedAt;
//        private List<GetLessonDetailResponse> lessons;
    }
}
