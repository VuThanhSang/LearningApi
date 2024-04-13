package com.example.learning_api.entity.sql.database;


import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "user_tokens")
public class UserTokenEntity {
    @Id
    private String id;
    private String userId;
    private String token;
    private Date expiresAt;
    private Date createdAt;
    private Date updatedAt;
}
