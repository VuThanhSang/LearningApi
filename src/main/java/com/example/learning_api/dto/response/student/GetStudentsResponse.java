package com.example.learning_api.dto.response.student;

import com.example.learning_api.entity.sql.database.StudentEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetStudentsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<StudentResponse> students;
    @Data
    public static class StudentResponse {
        private String id;
        private String userId;
        private String gradeLevel;
        public static StudentResponse formStudentEntity(StudentEntity studentEntity){
            StudentResponse studentResponse = new StudentResponse();
            studentResponse.setId(studentEntity.getId());
            studentResponse.setUserId(studentEntity.getUserId());
            studentResponse.setGradeLevel(studentEntity.getGradeLevel());
            return studentResponse;
        }
    }
}
