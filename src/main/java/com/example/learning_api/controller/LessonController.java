package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.lesson.CreateLessonRequest;
import com.example.learning_api.dto.request.lesson.UpdateLessonRequest;
import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.ILessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(LESSON_BASE_PATH)
public class LessonController {
    private final ILessonService lessonService;
    private final JwtService jwtService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    @PostMapping(path = "")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
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

    @GetMapping(path = "/section/{sectionId}")
    public ResponseEntity<ResponseAPI<List<GetLessonDetailResponse>>> getLessonBySectionId(@PathVariable String sectionId,@RequestHeader(name = "Authorization") String authorizationHeader) {
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String role = jwtService.extractRole(accessToken);
            String userId = jwtService.extractUserId(accessToken);
            String id="";
            if(role.equals("USER")){
                id = studentRepository.findByUserId(userId).getId();
            }
            else if(role.equals("TEACHER")){
                id = teacherRepository.findByUserId(userId).getId();
            }
            List<GetLessonDetailResponse> data= lessonService.getLessonBySectionId(sectionId,role,id);
            ResponseAPI<List<GetLessonDetailResponse>> res = ResponseAPI.<List<GetLessonDetailResponse>>builder()
                    .timestamp(new Date())
                    .data(data)
                    .message("Get lesson successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<List<GetLessonDetailResponse>> res = ResponseAPI.<List<GetLessonDetailResponse>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
}
