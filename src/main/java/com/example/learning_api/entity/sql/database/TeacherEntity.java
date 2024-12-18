package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.TeacherStatus;
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
    private String dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private Date HireDate;
    private String experience;
    private TeacherStatus status;
    private String createdAt;
    private String updatedAt;
    private UserEntity user;

    @DBRef
    private List<ClassRoomEntity> classRooms;
}
