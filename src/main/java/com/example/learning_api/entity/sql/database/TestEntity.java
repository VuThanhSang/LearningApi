package com.example.learning_api.entity.sql.database;

import com.example.learning_api.enums.TestShowResultType;
import com.example.learning_api.enums.TestStatus;
import com.example.learning_api.enums.TestType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "test")
public class TestEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private Integer duration;
    private String classroomId;
    private String teacherId;
    private String source;
    private TestShowResultType showResultType;
    private TestType type;
    private String startTime;
    private String endTime;
    private TestStatus status;
    private Integer attemptLimit;
    private Date createdAt;
    private Date updatedAt;

}
