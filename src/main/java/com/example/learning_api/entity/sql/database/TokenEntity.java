package com.example.learning_api.entity.sql.database;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "token")
public class TokenEntity {
    @Id
    private String id;
    @DBRef
    private UserEntity user;
    private String token;

    private boolean loggedOut;
}
