package com.example.learning_api.dto.request.deadline;

import com.example.learning_api.dto.response.deadline.UpcomingDeadlinesResponse;
import lombok.Data;

import java.util.List;

@Data
public class GetUpcomingDeadlineResponse {
    private long totalElements;
    private int totalPages;
    private List<UpcomingDeadlinesResponse> upcomingDeadlines;
}
