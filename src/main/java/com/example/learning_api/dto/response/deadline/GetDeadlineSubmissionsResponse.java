package com.example.learning_api.dto.response.deadline;

import com.example.learning_api.dto.common.FileDto;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
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
        private List<FileDto> attachment;
        private String submission;
        private String grade;
        private String feedback;
        private String status;
        private Date createdAt;
        private Date updatedAt;
        public static DeadlineSubmissionResponse fromDeadlineSubmissionEntity(DeadlineSubmissionsEntity deadlineSubmissionEntity){
            DeadlineSubmissionResponse deadlineSubmissionResponse = new DeadlineSubmissionResponse();
            deadlineSubmissionResponse.setId(deadlineSubmissionEntity.getId());
            deadlineSubmissionResponse.setTitle(deadlineSubmissionEntity.getTitle());
            deadlineSubmissionResponse.setDeadlineId(deadlineSubmissionEntity.getDeadlineId());
            deadlineSubmissionResponse.setStudentId(deadlineSubmissionEntity.getStudentId());
            deadlineSubmissionResponse.setAttachment(deadlineSubmissionEntity.getAttachments());
            deadlineSubmissionResponse.setSubmission(deadlineSubmissionEntity.getSubmission());
            deadlineSubmissionResponse.setGrade(deadlineSubmissionEntity.getGrade());
            deadlineSubmissionResponse.setFeedback(deadlineSubmissionEntity.getFeedback());
            deadlineSubmissionResponse.setStatus(String.valueOf(deadlineSubmissionEntity.getStatus()));
            return deadlineSubmissionResponse;
        }
    }
}
