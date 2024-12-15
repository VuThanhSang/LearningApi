package com.example.learning_api.controller;

import com.example.learning_api.dto.request.progress.ProgressCompleteRequest;
import com.example.learning_api.dto.response.notification.NotificationResponse;
import com.example.learning_api.entity.sql.database.ProgressEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/progress")
public class ProgressController {
    private final IProgressService progressService;

    @PostMapping("/complete-lesson")
    public ResponseEntity<ResponseAPI<String>> completeLesson(@RequestBody ProgressCompleteRequest request) {
        try {
            progressService.markLessonAsCompleted(request);
            return ResponseEntity.ok(
                    ResponseAPI.<String>builder()
                            .message("Complete lesson successfully")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error complete lesson", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<String>builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/complete-section")
    public ResponseEntity<ResponseAPI<String>> completeSection(@RequestBody ProgressCompleteRequest request) {
        try {
            progressService.markSectionAsCompleted(request);
            return ResponseEntity.ok(
                    ResponseAPI.<String>builder()
                            .message("Complete section successfully")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error complete section", e);
            return ResponseEntity.badRequest().body(
                    ResponseAPI.<String>builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }
}
