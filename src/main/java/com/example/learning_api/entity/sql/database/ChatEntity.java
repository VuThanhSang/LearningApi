package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.RoleEnum;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "chats")
public class ChatEntity {
    @Id
    private String id;
    private String meetingId;
    private String senderId;
    private String message;
    private Date timestamp;
    private RoleEnum role;
    private Date createdAt;
    private Date updatedAt;
}