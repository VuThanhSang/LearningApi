package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.feedback.CreateFeedbackAnswerRequest;
import com.example.learning_api.dto.request.feedback.CreateFeedbackRequest;
import com.example.learning_api.dto.request.feedback.UpdateFeedbackRequest;
import com.example.learning_api.dto.response.feedback.GetFeedBacksResponse;
import com.example.learning_api.dto.response.feedback.FeedbackAnswerResponse;
import com.example.learning_api.entity.sql.database.FeedbackEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IFeedbackService;
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
@RequestMapping("/api/v1/feedback")
public class FeedbackController {
    private final IFeedbackService feedbackService;

    @PostMapping(path = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createFeedback(@ModelAttribute("body") CreateFeedbackRequest body) {
        try {
            feedbackService.createFeedback(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        } catch (Exception e) {
            log.error("Error create  feedback: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error create feedback feedback")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping(path = "/{feedbackId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateFeedback(@PathVariable String feedbackId, @ModelAttribute @Valid UpdateFeedbackRequest body) {
        try {
            body.setId(feedbackId);
            feedbackService.updateFeedback( body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update  feedback successfully")
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

    @DeleteMapping(path = "/{feedbackId}")
    public ResponseEntity<ResponseAPI<String>> deleteFeedback(@PathVariable String feedbackId) {
        try {
            feedbackService.deleteFeedback(feedbackId);
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
    public ResponseEntity<ResponseAPI<FeedbackEntity>> getFeedbackById(@PathVariable String testId) {
        try {
            FeedbackEntity testFeedback = feedbackService.getFeedbackById(testId);
            ResponseAPI<FeedbackEntity> res = ResponseAPI.<FeedbackEntity>builder()
                    .timestamp(new Date())
                    .data(testFeedback)
                    .message("Get test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error get test feedback: ", e);
            ResponseAPI<FeedbackEntity> res = ResponseAPI.<FeedbackEntity>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/student/{studentId}/{formType}/{formId}")
    public ResponseEntity<ResponseAPI<List<FeedbackEntity>>> getFeedbacksByStudentIdAndTestId(@PathVariable String studentId,@PathVariable String formType, @PathVariable String formId) {
        try {
            formType = formType.toUpperCase();
            List<FeedbackEntity> testFeedback = feedbackService.getFeedbacksByStudentIdAndTestId(studentId, formId, formType);
            ResponseAPI<List<FeedbackEntity>> res = ResponseAPI.<List<FeedbackEntity>>builder()
                    .timestamp(new Date())
                    .data(testFeedback)
                    .message("Get test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error get test feedback: ", e);
            ResponseAPI<List<FeedbackEntity>> res = ResponseAPI.<List<FeedbackEntity>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{formType}/{formId}")
    public ResponseEntity<ResponseAPI<GetFeedBacksResponse>> getFeedbacksByTestId(@PathVariable String formType,@PathVariable String formId,
                                                                                      @RequestParam(defaultValue = "desc") String sort, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            formType = formType.toUpperCase();
            GetFeedBacksResponse testFeedbacks = feedbackService.getFeedbacksByTestId(formId,formType, sort, page-1, size);
            ResponseAPI<GetFeedBacksResponse> res = ResponseAPI.<GetFeedBacksResponse>builder()
                    .timestamp(new Date())
                    .data(testFeedbacks)
                    .message("Get test feedbacks successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error get test feedbacks: ", e);
            ResponseAPI<GetFeedBacksResponse> res = ResponseAPI.<GetFeedBacksResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/{feedbackId}/answer")
    public ResponseEntity<ResponseAPI<String>> createFeedbackAnswer(@PathVariable String  feedbackId,@RequestBody @Valid CreateFeedbackAnswerRequest body) {
        try {
            body.setFeedbackId(feedbackId);
            feedbackService.createFeedbackAnswer(body);
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

    @PatchMapping(path = "/{feedbackId}/answer")
    public ResponseEntity<ResponseAPI<String>> updateFeedbackAnswer(@PathVariable String feedbackId, @RequestBody String answer) {
        try {
            feedbackService.updateFeedbackAnswer(feedbackId, answer);
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

    @DeleteMapping(path = "/answer/{feedbackId}")
    public ResponseEntity<ResponseAPI<String>> deleteFeedbackAnswer(@PathVariable String feedbackId) {
        try {
            feedbackService.deleteFeedbackAnswer(feedbackId);
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

    @GetMapping(path = "/{feedbackId}/answer")
    public ResponseEntity<ResponseAPI<List<FeedbackAnswerResponse>>> getFeedbackAnswersByFeedbackId(@PathVariable String feedbackId) {
        try {
            List<FeedbackAnswerResponse> testFeedback = feedbackService.getFeedbackAnswersByFeedbackId(feedbackId);
            ResponseAPI<List<FeedbackAnswerResponse>> res = ResponseAPI.<List<FeedbackAnswerResponse>>builder()
                    .timestamp(new Date())
                    .data(testFeedback)
                    .message("Get test feedback successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            log.error("Error get test feedback: ", e);
            ResponseAPI<List<FeedbackAnswerResponse>> res = ResponseAPI.<List<FeedbackAnswerResponse>>builder()
                    .timestamp(new Date())
                    .message("Error get test feedback")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }


}
