package com.example.learning_api.dto.response.faq;

import com.example.learning_api.entity.sql.database.FileEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetFaqsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<Faq> data;
    @Data
    public static class Faq {
        private String id;
        private String question;
        private String userId;
        private String status;
        private String createdAt;
        private String updatedAt;
        private List<FileEntity> sources;

    }
}
