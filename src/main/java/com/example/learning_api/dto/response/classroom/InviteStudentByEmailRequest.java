package com.example.learning_api.dto.response.classroom;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class InviteStudentByEmailRequest {
    private String classroomId;
    private String teacherId;
    private List<String> emails;
    private String inviteType;
    private MultipartFile file;

}
