package com.example.learning_api.dto.response.section;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateSectionResponse {
    private String id;
    private String name;
    private String description;
    private String classRoomId;
}
