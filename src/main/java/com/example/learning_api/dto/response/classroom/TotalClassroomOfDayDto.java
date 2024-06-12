package com.example.learning_api.dto.response.classroom;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Data
public class TotalClassroomOfDayDto {
    private String _id;
    private long count;
    public TotalClassroomOfDayDto(String _id, long count) {
        this._id = _id;
        this.count = count;
    }

}