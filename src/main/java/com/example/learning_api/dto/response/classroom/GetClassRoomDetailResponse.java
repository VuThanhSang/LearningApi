package com.example.learning_api.dto.response.classroom;

import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetClassRoomDetailResponse {
    private ClassRoomEntity classRoom;
    private List<Section> sections;
    @Data
    public static class Section {
        private String id;
        private String name;
        private String description;
        private String status;
        private int index;
        private Boolean canAccess;
        private Boolean isComplete;
        private String createdAt;
        private String updatedAt;
//        private List<GetLessonDetailResponse> lessons;
    }
}
