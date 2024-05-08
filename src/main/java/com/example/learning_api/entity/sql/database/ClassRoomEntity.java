package com.example.learning_api.entity.sql.database;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "classrooms")
public class ClassRoomEntity {
    @Id
    private String id;
    @DBRef
    private CourseEntity course;
    private String name;
    private String description;
    private String image;
    private String teacherId;
    private Date createdAt;
    private Date updatedAt;
    @DBRef
    private List<SectionEntity> sections;
}
