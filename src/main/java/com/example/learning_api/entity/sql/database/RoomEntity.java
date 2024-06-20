package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "rooms")
public class RoomEntity {
    @Id
    private String id;
    private int capacity;
    private String location;
    private String name;
    private String createdAt;
    private String updatedAt;
}
