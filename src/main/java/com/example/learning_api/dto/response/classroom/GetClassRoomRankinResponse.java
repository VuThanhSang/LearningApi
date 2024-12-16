package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.enums.ClassRoomStatus;
import lombok.Data;

import java.util.List;

@Data
public class GetClassRoomRankinResponse {
    private Long totalElement;
    private Integer totalPage;
    private Double averageRating;
    private Integer totalReview;
    private List<Review> data;
    @Data
    public static class Review{
        private String id;
        private String title;
        private String comment;
        private Double rating;
        private String authorId;
        private String authorName;
        private String authorAvatar;
        private String classroomId;
        private String createdAt;
        private String updatedAt;
    }
}
