package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.ClassRoomStatus;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
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
    private Integer enrollmentCapacity;
    private Integer currentEnrollment;
    private String inviteCode;
    private ClassRoomStatus status;
    private Long duration;
    private Integer totalVideo;
    private Integer totalLesson;
    private Integer totalStudent;
    private Integer totalResource;
    private Integer totalAssignment;
    private Integer totalQuiz;
    private Integer totalExam;
    private Integer totalDocument;
    private Integer price;
    private String categoryId;
    private Boolean isPublic;
    private String teacherId;
    private String createdAt;
    private String updatedAt;


}
