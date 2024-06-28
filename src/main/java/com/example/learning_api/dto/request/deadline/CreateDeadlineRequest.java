package com.example.learning_api.dto.request.deadline;

import com.example.learning_api.enums.DeadlineType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class CreateDeadlineRequest {
    @NotBlank
    private String lessonId;
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private DeadlineType type;
    private MultipartFile file;
    private String dueDate;
}
