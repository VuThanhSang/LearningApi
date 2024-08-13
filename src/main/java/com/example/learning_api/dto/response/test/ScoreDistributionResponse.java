package com.example.learning_api.dto.response.test;


import com.example.learning_api.entity.sql.database.StudentEntity;
import lombok.Data;

import java.util.List;

@Data
public class ScoreDistributionResponse {
    private String fullname;
    private String studentId;
    private String email;
    private String phone;
    private Integer totalCorrect;
    private Integer totalIncorrect;
    private Integer totalAttempted;
    private Integer grade;
    private Integer attemptLimit;

}
