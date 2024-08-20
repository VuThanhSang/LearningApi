package com.example.learning_api.dto.response.feedback;

import com.example.learning_api.entity.sql.database.FeedbackEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetFeedBacksResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<FeedbackEntity> feedbacks;
}
