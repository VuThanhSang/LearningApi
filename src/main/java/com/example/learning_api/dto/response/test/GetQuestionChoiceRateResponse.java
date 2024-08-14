package com.example.learning_api.dto.response.test;

import lombok.Data;

import java.util.List;

@Data
public class GetQuestionChoiceRateResponse {
    private Integer totalPage;
    private Long totalElements;
    List<StatisticsResultResponse.Question> questions;
}
