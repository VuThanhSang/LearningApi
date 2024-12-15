package com.example.learning_api.dto.request.classroom;

import com.example.learning_api.enums.ClassRoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.learning_api.constant.SwaggerConstant.*;
@Data
@Builder
public class CreateClassRoomRequest {
    @NotBlank
    private String name;
    private String description;
    private MultipartFile image;
    @NotBlank
    private String teacherId;
    private ClassRoomStatus status;
    private Integer enrollmentCapacity;
    private Integer price;
    private List<String> categories;

}