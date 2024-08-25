package com.example.learning_api.dto.response.test;

import com.example.learning_api.entity.sql.database.QuestionEntity;
import com.example.learning_api.entity.sql.database.TestEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetTestInProgress {
    private Integer totalPage;
    private Long totalElements;
    private List<TestResponse> tests;
    @Data
    public static class TestResponse {
        private String id;
        private String name;
        private String description;
        private int duration;
        private String classroomId;
        private String startTime;
        private String endTime;
        private String showResultType;
        private int attemptLimit;
        private String status;
        private String teacherId;
        private String createdAt;
        private String updatedAt;

        public static GetTestInProgress.TestResponse fromTestEntity(TestEntity testEntity){
            GetTestInProgress.TestResponse testResponse = new GetTestInProgress.TestResponse();
            testResponse.setId(testEntity.getId());
            testResponse.setName(testEntity.getName());
            testResponse.setDescription(testEntity.getDescription());
            testResponse.setDuration(testEntity.getDuration());
            testResponse.setTeacherId(testEntity.getTeacherId());
            testResponse.setCreatedAt(testEntity.getCreatedAt().toString());
            testResponse.setUpdatedAt(testEntity.getUpdatedAt().toString());
            testResponse.setStartTime(testEntity.getStartTime().toString());
            testResponse.setEndTime(testEntity.getEndTime().toString());
            testResponse.setClassroomId(testEntity.getClassroomId());
            testResponse.setShowResultType(testEntity.getShowResultType().toString());
            return testResponse;
        }
    }
}
