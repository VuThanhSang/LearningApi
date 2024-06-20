package com.example.learning_api.dto.common;

import lombok.Data;

@Data
public class TotalTestOfDayDto {
    private String _id;
    private int count;
    public TotalTestOfDayDto(String _id, int count) {
        this._id = _id;
        this.count = count;
    }

}
