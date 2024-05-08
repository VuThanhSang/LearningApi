package com.example.learning_api.dto.request.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import static com.example.learning_api.constant.SwaggerConstant.*;

@Data
@Builder
public class CreateTeacherRequest {
    @Schema(example = NAME_EX )
    @NotBlank
    private String userId;
    private String bio;
    private String qualifications;

}
