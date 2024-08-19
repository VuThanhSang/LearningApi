package com.example.learning_api.dto.request.deadline;

import com.example.learning_api.dto.common.SourceUploadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateDeadlineSubmissionsRequest {
    @NotBlank
    private String id;
    private String title;
    private List<SourceUploadDto> files;
    private String submission;
    private String grade;
    private String feedback;
    private String status;
}
