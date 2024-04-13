package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "faqs")
public class FAQEntity {
    @Id
    private String id;
    private String courseId;
    private String question;
    private String answer;
    private Date createdAt;
    private Date updatedAt;
}
