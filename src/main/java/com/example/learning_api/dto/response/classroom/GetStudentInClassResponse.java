package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.repository.database.StudentRepository;
import lombok.Data;

import java.util.List;


@Data
public class GetStudentInClassResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<StudentResponse> students;
    @Data
    public static class StudentResponse {
        private String id;
        private String userId;
        private String gradeLevel;
        private String gender;
        private String address;
        private String phone;
        private String academicYearId;
        private String dateOfBirth;
        private String majorId;
        private UserEntity user;
        private Integer progress;
        public static StudentResponse formStudentEntity(StudentEntity userEntity) {
            StudentResponse studentResponse = new StudentResponse();
            studentResponse.setId(userEntity.getId());
            studentResponse.setUserId(userEntity.getUserId());
            studentResponse.setGradeLevel(userEntity.getGradeLevel());
            studentResponse.setGender(userEntity.getGender());
            studentResponse.setAddress(userEntity.getAddress());
            studentResponse.setPhone(userEntity.getPhone());
            studentResponse.setAcademicYearId(userEntity.getAcademicYearId());
            studentResponse.setDateOfBirth(userEntity.getDateOfBirth());
            studentResponse.setMajorId(userEntity.getMajorId());
            studentResponse.setUser(userEntity.getUser());
            return studentResponse;
        }
    }

}
