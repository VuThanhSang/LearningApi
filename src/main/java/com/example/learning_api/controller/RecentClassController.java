package com.example.learning_api.controller;

import com.example.learning_api.entity.sql.database.RecentClassEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IRecentClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(RECENT_CLASS_BASE_PATH)
public class RecentClassController {
    private final IRecentClassService recentClassService;

    @PostMapping(path = "")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<String>> createRecentClass(@RequestBody RecentClassEntity body) {
        try {
            recentClassService.createRecentClass(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create recent class successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PatchMapping(path = "/{studentId}/{classroomId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<String>> updateRecentClass(@PathVariable String studentId, @PathVariable String classroomId, @RequestBody Date lastAccessedAt) {
        try {
            recentClassService.updateRecentClass(studentId, classroomId, lastAccessedAt);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update recent class successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<String>> deleteRecentClass(@PathVariable String id) {
        try {
            recentClassService.deleteRecentClass(id);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete recent class successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }
}
