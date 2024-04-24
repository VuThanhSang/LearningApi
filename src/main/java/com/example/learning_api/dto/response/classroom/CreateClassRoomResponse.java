package com.example.learning_api.dto.response.classroom;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateClassRoomResponse {
    private String id;
    private String name;
    private String description;
    private String image;
}
