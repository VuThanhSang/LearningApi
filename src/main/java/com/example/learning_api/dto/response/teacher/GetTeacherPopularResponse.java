package com.example.learning_api.dto.response.teacher;

import lombok.Data;

@Data
public class GetTeacherPopularResponse {
    private String id;
    private String fullname;
    private String avatar;
    private String experience;
    private Long numberStudent;
}
