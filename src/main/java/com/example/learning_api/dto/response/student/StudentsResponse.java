package com.example.learning_api.dto.response.student;

import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.enums.StudentStatus;
import lombok.Data;

import java.util.List;

@Data
public class StudentsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<StudentResponse> students;
    @Data
    public static class StudentResponse {
        private String id;
        private String userId;
        private String gradeLevel;
        private String studentCode;
        private String gender;
        private String address;
        private String dateOfBirth;
        private String phone;
        private String schoolYear;
        private String academicYearId;
        private String majorId;
        private String email;
        private String fullName;
        private String avatar;
        private StudentStatus status;
        private String createdAt;
        private String updatedAt;

        public static StudentResponse formStudentEntity(StudentEntity userEntity) {
            StudentResponse studentResponse = new StudentResponse();
            studentResponse.setId(userEntity.getId());
            studentResponse.setUserId(userEntity.getUserId());
            studentResponse.setGradeLevel(userEntity.getGradeLevel());
            studentResponse.setStudentCode(userEntity.getStudentCode());
            studentResponse.setGender(userEntity.getGender());
            studentResponse.setAddress(userEntity.getAddress());
            studentResponse.setDateOfBirth(userEntity.getDateOfBirth());
            studentResponse.setPhone(userEntity.getPhone());
            studentResponse.setSchoolYear(userEntity.getSchoolYear());
            studentResponse.setAcademicYearId(userEntity.getAcademicYearId());
            studentResponse.setMajorId(userEntity.getMajorId());

                studentResponse.setEmail(userEntity.getUser().getEmail());
                studentResponse.setFullName(userEntity.getUser().getFullname());
                studentResponse.setAvatar(userEntity.getUser().getAvatar());
            studentResponse.setStatus(userEntity.getStatus());
            studentResponse.setCreatedAt(userEntity.getCreatedAt());
            studentResponse.setUpdatedAt(userEntity.getUpdatedAt());
            return studentResponse;
        }
    }
}
