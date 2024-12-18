package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetApprovalClassroomResponse {
    private int totalPage;
    private int totalElement;
    private List<ApprovalRequest> data;


    @Data
    public static class ApprovalRequest {
        private String id;
        private String classroomId;
        private ClassRoomEntity classroom;
        private String teacherId;
        private UserEntity teacher;
        private String status;
        private String createdAt;
        private String updatedAt;
    }



}
