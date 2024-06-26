package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "subject_specializations")
public class SubjectSpecializationEntity {
    @Id
    private String id;
    private String majorsId;
    private String teacherId;

}
