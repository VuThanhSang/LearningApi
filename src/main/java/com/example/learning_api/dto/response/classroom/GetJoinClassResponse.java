package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.entity.sql.database.StudentEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetJoinClassResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<JoinRequest> joinRequests;

    @Data
    public static class JoinRequest {
        private String id;
        private StudentEntity student;
        private String classroomId;
        private String status;
        private String createdAt;
        private String updatedAt;
    }
}
