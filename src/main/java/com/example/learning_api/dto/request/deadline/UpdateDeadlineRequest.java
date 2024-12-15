package com.example.learning_api.dto.request.deadline;


import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.DeadlineType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class UpdateDeadlineRequest {
    @NotBlank
    private String id;
    private String title;
    private String description;
    private String status;
    private List<MultipartFile> files;
    private String type;

}
