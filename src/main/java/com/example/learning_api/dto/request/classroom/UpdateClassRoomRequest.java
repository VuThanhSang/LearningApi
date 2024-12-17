package com.example.learning_api.dto.request.classroom;

import com.example.learning_api.enums.ClassRoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.learning_api.constant.SwaggerConstant.*;
import static com.example.learning_api.constant.SwaggerConstant.TEACHER_ID_EX;


@Data
public class UpdateClassRoomRequest {
    @Schema(example = ID_EX)
    @NotBlank
    private String id;
    @Schema(example = NAME_EX)
    private String name;
    @Schema(example = DESCRIPTION_EX)
    private String description;
    private MultipartFile image;
    @Schema(example = TEACHER_ID_EX)
    private String teacherId;
    private Integer enrollmentCapacity;
    private Integer price;
    private Integer duration;
    private String categoryId;
    private ClassRoomStatus status;
}
