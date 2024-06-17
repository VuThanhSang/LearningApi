package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "majors")
public class MajorsEntity {
    @Id
    private String id;
    private String name;
    private String description;
}
