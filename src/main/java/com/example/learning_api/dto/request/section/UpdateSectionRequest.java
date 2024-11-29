package com.example.learning_api.dto.request.section;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.example.learning_api.constant.SwaggerConstant.*;
import static com.example.learning_api.constant.SwaggerConstant.ID_EX;

@Data
public class UpdateSectionRequest {
    @Schema(example = ID_EX)
    @NotBlank
    private String id;
    @Schema(example = NAME_EX)
    private String name;
    @Schema(example = DESCRIPTION_EX)
    private String description;
    @Schema(example = ID_EX)
    private String classRoomId;
    private String status;
    private Integer index;
}
