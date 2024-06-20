package com.example.learning_api.dto.request.course;

import com.example.learning_api.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static com.example.learning_api.constant.SwaggerConstant.*;
import static com.example.learning_api.constant.SwaggerConstant.TEACHER_ID_EX;

@Data
@Builder
public class CreateCourseRequest {
    @Schema(example = ID_EX)
    private String id;
    @Schema(example = NAME_EX)
    @NotBlank
    private String name;
    @Schema(example = DESCRIPTION_EX)
    private String description;
    private MultipartFile thumbnail;
    private MultipartFile videoIntro;
    private String termId;
    private CourseStatus status;
}
