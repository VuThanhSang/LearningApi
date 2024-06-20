package com.example.learning_api.dto.response.course;


import com.example.learning_api.entity.sql.database.CourseEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetCoursesResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<CourseResponse> courses;
    @Data
    public static class CourseResponse {
        private String id;
        private String name;
        private String description;
        private String createdAt;
        private String updatedAt;
        private String teacherId;
        private String startDate;
        private String endDate;
        public static CourseResponse formCourseEntity(CourseEntity courseEntity){
            CourseResponse courseResponse = new CourseResponse();
            courseResponse.setId(courseEntity.getId());
            courseResponse.setName(courseEntity.getName());
            courseResponse.setDescription(courseEntity.getDescription());
            courseResponse.setCreatedAt(courseEntity.getCreatedAt().toString());
            courseResponse.setUpdatedAt(courseEntity.getUpdatedAt().toString());
            return courseResponse;
        }
    }
}
