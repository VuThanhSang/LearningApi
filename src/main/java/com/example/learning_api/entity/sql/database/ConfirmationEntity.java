package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.ConfirmationCodeStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Document(collection = "confirmation")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationEntity {
    @MongoId
    private String id;

    private String code;

    private String email;

    private ConfirmationCodeStatus status;

    private Date expireAt;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}