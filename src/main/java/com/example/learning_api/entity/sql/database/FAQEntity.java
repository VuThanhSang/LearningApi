package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FaqStatus;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "faqs")
public class FAQEntity {
    @Id
    private String id;
    private String classId;
    private String question;
    private String userId;
    private FaqStatus status;
    private List<SourceDto> sources;
    private String createdAt;
    private String updatedAt;
    @Data
    public static class SourceDto {
        private FaqSourceType type;
        private String path;
    }

}
