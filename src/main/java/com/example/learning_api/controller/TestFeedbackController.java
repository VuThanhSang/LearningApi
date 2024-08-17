package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackAnswerRequest;
import com.example.learning_api.dto.request.test_feedback.CreateTestFeedbackRequest;
import com.example.learning_api.dto.request.test_feedback.UpdateTestFeedbackRequest;
import com.example.learning_api.dto.response.test.CreateTestResponse;
import com.example.learning_api.dto.response.test_feedback.GetTestFeedBacksResponse;
import com.example.learning_api.dto.response.test_feedback.TestFeedbackAnswerResponse;
import com.example.learning_api.entity.sql.database.TestFeedbackAnswerEntity;
import com.example.learning_api.entity.sql.database.TestFeedbackEntity;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/test-feedback")
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
    @PatchMapping(path = "/{testFeedbackId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateTestFeedback(@PathVariable String testFeedbackId, @ModelAttribute @Valid UpdateTestFeedbackRequest body) {
        try {
            body.setId(testFeedbackId);
            testFeedbackService.updateTestFeedback( body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
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
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{testId}")
    public ResponseEntity<ResponseAPI<TestFeedbackEntity>> getTestFeedbackById(@PathVariable String testId) {
        try {
            TestFeedbackEntity testFeedback = testFeedbackService.getTestFeedbackById(testId);
            ResponseAPI<TestFeedbackEntity> res = ResponseAPI.<TestFeedbackEntity>builder()
                    .timestamp(new Date())
                    .data(testFeedback)
                    .message("Get test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error get test feedback: ", e);
            ResponseAPI<TestFeedbackEntity> res = ResponseAPI.<TestFeedbackEntity>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/student/{studentId}/test/{testId}")
    public ResponseEntity<ResponseAPI<List<TestFeedbackEntity>>> getTestFeedbacksByStudentIdAndTestId(@PathVariable String studentId, @PathVariable String testId) {
        try {
            List<TestFeedbackEntity> testFeedback = testFeedbackService.getTestFeedbacksByStudentIdAndTestId(studentId, testId);
            ResponseAPI<List<TestFeedbackEntity>> res = ResponseAPI.<List<TestFeedbackEntity>>builder()
                    .timestamp(new Date())
                    .data(testFeedback)
                    .message("Get test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error get test feedback: ", e);
            ResponseAPI<List<TestFeedbackEntity>> res = ResponseAPI.<List<TestFeedbackEntity>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/test/{testId}")
    public ResponseEntity<ResponseAPI<GetTestFeedBacksResponse>> getTestFeedbacksByTestId(@PathVariable String testId,
          @RequestParam(defaultValue = "desc") String sort, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            GetTestFeedBacksResponse testFeedbacks = testFeedbackService.getTestFeedbacksByTestId(testId, sort, page-1, size);
            ResponseAPI<GetTestFeedBacksResponse> res = ResponseAPI.<GetTestFeedBacksResponse>builder()
                    .timestamp(new Date())
                    .data(testFeedbacks)
                    .message("Get test feedbacks successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error get test feedbacks: ", e);
            ResponseAPI<GetTestFeedBacksResponse> res = ResponseAPI.<GetTestFeedBacksResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/{testFeedbackId}/answer")
    public ResponseEntity<ResponseAPI<String>> createTestFeedbackAnswer(@PathVariable String  testFeedbackId,@RequestBody @Valid CreateTestFeedbackAnswerRequest body) {
        try {
            body.setTestFeedbackId(testFeedbackId);
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
                    .message(e.getMessage())
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

    @DeleteMapping(path = "/answer/{testFeedbackId}")
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

    @GetMapping(path = "/{testFeedbackId}/answer")
    public ResponseEntity<ResponseAPI<List<TestFeedbackAnswerResponse>>> getTestFeedbackAnswersByFeedbackId(@PathVariable String testFeedbackId) {
        try {
            List<TestFeedbackAnswerResponse> testFeedback = testFeedbackService.getTestFeedbackAnswersByFeedbackId(testFeedbackId);
            ResponseAPI<List<TestFeedbackAnswerResponse>> res = ResponseAPI.<List<TestFeedbackAnswerResponse>>builder()
                    .timestamp(new Date())
                    .data(testFeedback)
                    .message("Get test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error get test feedback: ", e);
            ResponseAPI<List<TestFeedbackAnswerResponse>> res = ResponseAPI.<List<TestFeedbackAnswerResponse>>builder()
                    .timestamp(new Date())
                    .message("Error get test feedback")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }


}
