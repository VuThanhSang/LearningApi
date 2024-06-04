package com.example.learning_api.dto.request.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import static com.example.learning_api.constant.SwaggerConstant.*;


@Data
public class UpdateCourseRequest {
    @Schema(example = ID_EX)
    @NotBlank
    private String id;
    @Schema(example = NAME_EX)
    private String name;
    @Schema(example = DESCRIPTION_EX)
    private String description;
    @Schema(example = IMAGE_EX)
    private MultipartFile image;
    @Schema(example = TEACHER_ID_EX)
    private String teacherId;
    private String startDate;
    private String endDate;

}
