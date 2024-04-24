package com.example.learning_api.dto.request.section;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import static com.example.learning_api.constant.SwaggerConstant.*;
import static com.example.learning_api.constant.SwaggerConstant.TEACHER_ID_EX;


@Data
@Builder
public class CreateSectionRequest {
    @Schema(example = NAME_EX)
    @NotBlank
    private String name;
    @Schema(example = DESCRIPTION_EX)
    private String description;
    @Schema(example = ID_EX)
    @NotBlank
    private String classRoomId;
}
