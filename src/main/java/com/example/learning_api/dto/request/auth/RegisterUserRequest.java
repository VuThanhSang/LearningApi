package com.example.learning_api.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.example.learning_api.constant.SwaggerConstant.*;

@Data
@NoArgsConstructor  // Add this annotation to generate a default constructor
public class RegisterUserRequest {
    @Schema(example = EMAIL_EX)
    @Email
    @NotBlank
    private String email;

    @Schema(example = PASSWORD_EX)
    @NotBlank
    @Size(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    private String password;

    @Schema(example = USERNAME_EX)
    @NotBlank
    private String username;

    @Schema(example = FULLNAME_EX)
    @NotBlank
    private String fullname;

    @Schema(example = ROLE_EX)
    @NotBlank
    private String role;

    @Schema(example = PHONE_NUMBER_EX)
    @Pattern(regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;
}