package com.example.learning_api.dto.response.ClassRoomResponse;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateClassRoomResponse {
    private String name;
    private String description;
    private String image;
}
