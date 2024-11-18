package com.example.learning_api.dto.response.deadline;

import com.example.learning_api.dto.common.FileDto;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetDeadlineSubmissionsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<DeadlineSubmissionResponse> deadlineSubmissions;
    @Data
    public static class DeadlineSubmissionResponse {
        private String id;
        private String title;
        private String deadlineId;
        private String studentId;
        private String studentName;
        private String studentEmail;
        private String studentAvatar;
        private List<FileEntity> files;
        private String submission;
        private Boolean isLate;
        private String grade;
        private String feedback;
        private String status;
        private String createdAt;
        private String updatedAt;

    }
}
