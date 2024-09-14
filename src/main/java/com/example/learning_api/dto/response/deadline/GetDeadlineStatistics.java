package com.example.learning_api.dto.response.deadline;

import lombok.Data;

import java.util.List;

@Data
public class GetDeadlineStatistics {
    private Integer totalPage;
    private Long totalElements;
    private List<DeadlineStatistics> deadlineStatistics;
}
