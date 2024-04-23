package com.example.learning_api.dto.request.classroom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.example.learning_api.constant.SwaggerConstant.*;

@Data
public class DeleteClassRoomRequest {
    @Schema(example = ID_EX)
    @NotBlank
    private String id;
}
