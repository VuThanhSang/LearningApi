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
@Document(collection = "courses")
public class CourseEntity {
    @Id
    private String id;
    private String name;
    private String description;
    @DBRef
    private TeacherEntity teacher;
    private Date createdAt;
    private Date updatedAt;
    @DBRef
    private List<ClassRoomEntity> classRooms;
    @DBRef
    private List<FAQEntity> faqs;
}
