package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.ClassRoomStatus;
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

    private String name;
    private String description;
    private String image;
    private String facultyId;
    private Integer enrollmentCapacity;
    private Integer currentEnrollment;
    private String inviteCode;
    private ClassRoomStatus status;
    private Integer credits;
    private String termId;
    private String teacherId;
    private String createdAt;
    private String updatedAt;

}
