package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.question.CreateQuestionRequest;
import com.example.learning_api.dto.request.question.UpdateQuestionRequest;
import com.example.learning_api.dto.response.question.CreateQuestionResponse;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.dto.response.test.GetTestsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(QUESTION_BASE_PATH)
public class QuestionController {
    final IQuestionService questionService;

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<CreateQuestionResponse>> createQuestion(@ModelAttribute @Valid CreateQuestionRequest body) {
        try{
            CreateQuestionResponse resDate = questionService.createQuestion(body);
            ResponseAPI<CreateQuestionResponse> res = ResponseAPI.<CreateQuestionResponse>builder()
                    .timestamp(new Date())
                    .message("Create question successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateQuestionResponse> res = ResponseAPI.<CreateQuestionResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @PatchMapping(path = "/{questionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateQuestion(@RequestBody @Valid UpdateQuestionRequest body, @PathVariable String questionId) {
        try{
            body.setId(questionId);
            questionService.updateQuestion(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update question successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @DeleteMapping(path = "/{questionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> deleteQuestion(@PathVariable String questionId) {
        try{
            questionService.deleteQuestion(questionId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete question successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetQuestionsResponse>> getQuestions (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ){
        try{
            GetQuestionsResponse resDate = questionService.getQuestions(page-1, size, search);
            ResponseAPI<GetQuestionsResponse> res = ResponseAPI.<GetQuestionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get questions successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetQuestionsResponse> res = ResponseAPI.<GetQuestionsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
}
