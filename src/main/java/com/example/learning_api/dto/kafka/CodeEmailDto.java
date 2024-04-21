package com.example.learning_api.dto.kafka;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeEmailDto {
    private String code;
    private String email;
}