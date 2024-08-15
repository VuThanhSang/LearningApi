package com.example.learning_api.dto.response.test_feedback;

import com.example.learning_api.entity.sql.database.TestFeedbackEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetTestFeedBacksResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<TestFeedbackEntity> testFeedbacks;
}
