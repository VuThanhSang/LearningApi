package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "carts")
public class CartEntity {
    @Id
    private String id;
    private String userId;
    private String classroomId;
    private String createdAt;
    private String updatedAt;

}
