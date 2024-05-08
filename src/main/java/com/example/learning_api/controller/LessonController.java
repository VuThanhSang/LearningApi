package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.lesson.CreateLessonRequest;
import com.example.learning_api.dto.request.lesson.UpdateLessonRequest;
import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ILessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(LESSON_BASE_PATH)
public class LessonController {
    private final ILessonService lessonService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> createLesson(@RequestBody @Valid CreateLessonRequest body) {
        try{
            lessonService.createLesson(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create lesson successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @PatchMapping(path = "/{lessonId}")
    public ResponseEntity<ResponseAPI<String>> updateLesson(@RequestBody @Valid UpdateLessonRequest body, @PathVariable String lessonId) {
        try{
            body.setId(lessonId);
            lessonService.updateLesson(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update lesson successfully")
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

    @DeleteMapping(path = "/{lessonId}")
    public ResponseEntity<ResponseAPI<String>> deleteLesson(@PathVariable String lessonId) {
        try{
            lessonService.deleteLesson(lessonId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete lesson successfully")
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

    @GetMapping(path = "/{lessonId}")
    public ResponseEntity<ResponseAPI<GetLessonDetailResponse>> getLessonWithResourcesAndMediaAndSubstances(@PathVariable String lessonId) {
        try{
            GetLessonDetailResponse data= lessonService.getLessonWithResourcesAndMediaAndSubstances(lessonId);
            ResponseAPI<GetLessonDetailResponse> res = ResponseAPI.<GetLessonDetailResponse>builder()
                    .timestamp(new Date())
                    .data(data)
                    .message("Get lesson successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetLessonDetailResponse> res = ResponseAPI.<GetLessonDetailResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
}
