package com.example.learning_api.dto.response.forum;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class GetTagsResponse {
    private int totalPage;
    private int totalElement;
    private List<Tag> tags;
    @Data
    public static class Tag {
        private String id;
        private String name;
        private String classId;
        private Boolean isForClass;
        private int postCount;
        private String createdAt;
        private String updatedAt;
    }
}
