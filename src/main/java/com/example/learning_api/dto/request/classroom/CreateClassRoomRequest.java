package com.example.learning_api.dto.request.classroom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import static com.example.learning_api.constant.SwaggerConstant.*;
@Data
@Builder

public class CreateClassRoomRequest {
    @Schema(example = NAME_EX)
    @NotBlank
    private String name;
    @Schema(example = DESCRIPTION_EX)
    private String description;
    @Schema(example = IMAGE_EX)
    private MultipartFile image;
    @Schema(example = TEACHER_ID_EX)
    @NotBlank
    private String teacherId;

}
