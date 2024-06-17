package com.example.learning_api.dto.request.term;

import lombok.Data;

@Data
public class CreateTermRequest {
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String academicYearId;
    private String majorId;
}
