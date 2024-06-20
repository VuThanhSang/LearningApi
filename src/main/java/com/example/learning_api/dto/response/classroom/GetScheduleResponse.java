package com.example.learning_api.dto.response.classroom;
import lombok.Data;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class GetScheduleResponse {
    private String dayOfWeek;
    private List<Session> sessions;
    public GetScheduleResponse(String dayOfWeek, List<Session> sessions) {
        this.dayOfWeek = dayOfWeek;
        this.sessions = sessions;
    }
    @Data
    public static class Session {
        private String startTime;
        private String endTime;
        private String className;
        private String classroomId;

        public Session(String startTime, String endTime, String className) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.className = className;
        }
        // getters and setters
    }

    // Constructors, getters, and setters
}