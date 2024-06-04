package com.example.learning_api.dto.request.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestSubmitRequest {
    @NotBlank
    private String studentId;
    @NotBlank
    private String testId;
    @NotBlank
    private List<List<Integer>> answers;


}
