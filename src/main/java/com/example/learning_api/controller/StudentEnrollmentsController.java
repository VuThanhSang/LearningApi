package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.student_enrollments.EnrollStudentRequest;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IStudentEnrollmentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(STUDENT_ENROLLMENTS_BASE_PATH)
@Slf4j
public class StudentEnrollmentsController {
    private final IStudentEnrollmentsService studentEnrollmentsService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> enrollStudent(@RequestBody @Valid EnrollStudentRequest body) {
        try {
            studentEnrollmentsService.enrollStudent(body.getStudentId(), body.getCourseId());
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Enroll student successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{studentId}/drop")
    public ResponseEntity<ResponseAPI<String>> dropStudent(@PathVariable String studentId, @RequestParam String courseId) {
        try {
            studentEnrollmentsService.dropStudent(studentId, courseId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Drop student successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{studentId}/complete")
    public ResponseEntity<ResponseAPI<String>> completeStudent(@PathVariable String studentId, @RequestParam String courseId) {
        try {
            studentEnrollmentsService.completeStudent(studentId, courseId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Complete student successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{studentId}/grade")
    public ResponseEntity<ResponseAPI<String>> updateStudentGrade(@PathVariable String studentId, @RequestParam String courseId, @RequestParam int grade) {
        try {
            studentEnrollmentsService.updateStudentGrade(studentId, courseId, grade);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update student grade successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
}
