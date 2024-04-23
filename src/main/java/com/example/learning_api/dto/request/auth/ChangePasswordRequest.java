package com.example.learning_api.dto.request.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import static com.example.learning_api.constant.SwaggerConstant.*;

@Data
public class ChangePasswordRequest {

    @Schema(example = EMAIL_EX)
    @NotBlank
    private String email;

    @Schema(example = CODE_EX)
    @NotBlank
    private String code;

    @Schema(example = PASSWORD_USER_EX)
    @NotBlank
    @Size(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    private String password;
}
