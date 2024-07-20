package com.example.learning_api.dto.request.test;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class CreateTestResultRequest {
    @NotNull
    private String studentId;
    @NotNull
    private String testId;


}
