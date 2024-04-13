package com.example.learning_api.entity.sql.database;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@NoArgsConstructor
@Document(collection = "users")
public class UserEntity {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String fullname;
    private String role;
    private Date createdAt;
    private Date updatedAt;
}
