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
@Document(collection = "students")
public class StudentEntity {
    @Id
    private String id;
    private String userId;
    private String gradeLevel;
    private UserEntity user;
    private String gender;
    private String address;
    private String phone;
    private String schoolYear;
    private String academicYearId;
    private String majorId;
    private Date createdAt;
    private Date updatedAt;
    @DBRef
    private List<ClassRoomEntity> classRooms;
}
