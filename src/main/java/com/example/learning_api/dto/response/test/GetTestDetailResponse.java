package com.example.learning_api.dto.response.test;

import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.entity.sql.database.FileEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetTestDetailResponse {
    private int totalQuestions;
    private String name;
    private String description;
    private FileEntity source;
    private String id;
    private int duration;
    private String startTime;
    private String endTime;
    private String classroomId;
    private int attemptLimit;
    private String teacherId;
    private String showResultType;
    private List<GetQuestionsResponse.QuestionResponse> questions;
}
