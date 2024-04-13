package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "intension_rooms")
public class IntensionRoomEntity {
    @Id
    private String id;
    private String classroomId;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}
