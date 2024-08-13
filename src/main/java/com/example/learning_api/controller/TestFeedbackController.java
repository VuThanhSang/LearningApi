package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackAnswerRequest;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackRequest;
import com.example.learning_api.dto.request.test_feedback.UpdateTestFeedbackRequest;
import com.example.learning_api.dto.response.test.CreateTestResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ITestFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test-feedback")
public class TestFeedbackController {
    private final ITestFeedbackService testFeedbackService;

    @PostMapping(path = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createTestFeedback(@ModelAttribute("body") CreateTestFeedbackRequest body) {
        try {
            testFeedbackService.createTestFeedback(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create test successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        } catch (Exception e) {
            log.error("Error create test feedback: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error create test feedback")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping(path = "/{testFeedbackId}")
    public ResponseEntity<ResponseAPI<String>> updateTestFeedback(@PathVariable String testFeedbackId, @RequestBody @Valid UpdateTestFeedbackRequest body) {
        try {
            body.setId(testFeedbackId);
            testFeedbackService.updateTestFeedback( body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error update test feedback: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error update test feedback")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{testFeedbackId}")
    public ResponseEntity<ResponseAPI<String>> deleteTestFeedback(@PathVariable String testFeedbackId) {
        try {
            testFeedbackService.deleteTestFeedback(testFeedbackId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error delete test feedback: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error delete test feedback")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/answer")
    public ResponseEntity<ResponseAPI<String>> createTestFeedbackAnswer(@RequestBody @Valid CreateTestFeedbackAnswerRequest body) {
        try {
            testFeedbackService.createTestFeedbackAnswer(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create test feedback answer successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        } catch (Exception e) {
            log.error("Error create test feedback answer: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error create test feedback answer")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/{testFeedbackId}/answer")
    public ResponseEntity<ResponseAPI<String>> updateTestFeedbackAnswer(@PathVariable String testFeedbackId, @RequestBody String answer) {
        try {
            testFeedbackService.updateTestFeedbackAnswer(testFeedbackId, answer);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update test feedback answer successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error update test feedback answer: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error update test feedback answer")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{testFeedbackId}/answer")
    public ResponseEntity<ResponseAPI<String>> deleteTestFeedbackAnswer(@PathVariable String testFeedbackId) {
        try {
            testFeedbackService.deleteTestFeedbackAnswer(testFeedbackId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete test feedback answer successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error delete test feedback answer: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error delete test feedback answer")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }


}
