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
        private String dateOfBirth;
        private String gender;
        private String phone;
        private String address;
        private String experience;
        private String status;
        private String createdAt;
        private String updatedAt;
        public static TeacherResponse formTeacherEntity(TeacherEntity teacherEntity){
            TeacherResponse teacherResponse = new TeacherResponse();
            teacherResponse.setId(teacherEntity.getId());
            teacherResponse.setUserId(teacherEntity.getUserId());
            teacherResponse.setBio(teacherEntity.getBio());
            teacherResponse.setQualifications(teacherEntity.getQualifications());
            teacherResponse.setDateOfBirth(teacherEntity.getDateOfBirth());
            teacherResponse.setGender(teacherEntity.getGender());
            teacherResponse.setPhone(teacherEntity.getPhone());
            teacherResponse.setAddress(teacherEntity.getAddress());
            teacherResponse.setExperience(teacherEntity.getExperience());
            teacherResponse.setStatus(teacherEntity.getStatus().name());
            teacherResponse.setCreatedAt(teacherEntity.getCreatedAt().toString());
            teacherResponse.setUpdatedAt(teacherEntity.getUpdatedAt().toString());
            return teacherResponse;
        }
    }

}
