package com.example.learning_api.dto.response.teacher;

import com.example.learning_api.entity.sql.database.TeacherEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetTeachersResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<TeacherResponse> teachers;
    @Data
    public static class TeacherResponse {
        private String id;
        private String userId;
        private String bio;
        private String qualifications;
        public static TeacherResponse formTeacherEntity(TeacherEntity teacherEntity){
            TeacherResponse teacherResponse = new TeacherResponse();
            teacherResponse.setId(teacherEntity.getId());
            teacherResponse.setUserId(teacherEntity.getUserId());
            teacherResponse.setBio(teacherEntity.getBio());
            teacherResponse.setQualifications(teacherEntity.getQualifications());
            return teacherResponse;
        }
    }

}
