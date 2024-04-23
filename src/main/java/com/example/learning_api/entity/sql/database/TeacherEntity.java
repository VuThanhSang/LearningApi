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
@Document(collection = "teachers")
public class TeacherEntity {
    @Id
    private String id;
    private String userId;
    private String bio;
    private String qualifications;
    private Date createdAt;
    private Date updatedAt;
    @DBRef
    private List<CourseEntity> courses;
    @DBRef
    private List<ClassRoomEntity> classRooms;
}
