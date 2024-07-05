package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.FacultyStatus;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "faculties")
public class FacultyEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private String dean;
    private FacultyStatus status;
    private Date createdAt;
    private Date updatedAt;
}
