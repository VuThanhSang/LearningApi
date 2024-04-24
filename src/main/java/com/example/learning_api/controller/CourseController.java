package com.example.learning_api.controller;


import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.course.CreateCourseRequest;
import com.example.learning_api.dto.request.course.DeleteCourseRequest;
import com.example.learning_api.dto.request.course.UpdateCourseRequest;
import com.example.learning_api.dto.response.course.CreateCourseResponse;
import com.example.learning_api.dto.response.course.GetCoursesResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ICourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.COURSE_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(COURSE_BASE_PATH)
@Slf4j
public class CourseController {
    private final ICourseService courseService;
    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<CreateCourseResponse>> createCourse(@RequestBody @Valid CreateCourseRequest body) {
        try{
            CreateCourseResponse resDate = courseService.createCourse(body);
            ResponseAPI<CreateCourseResponse> res = ResponseAPI.<CreateCourseResponse>builder()
                    .timestamp(new Date())
                    .message("Create course successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateCourseResponse> res = ResponseAPI.<CreateCourseResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @PatchMapping(path = "/{courseId}")
    public ResponseEntity<ResponseAPI<String>> updateCourse(@RequestBody @Valid UpdateCourseRequest body, @PathVariable String courseId) {
        try{
            body.setId(courseId);
            courseService.updateCourse(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update course successfully")
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
    @DeleteMapping(path = "/{courseId}")
    public ResponseEntity<ResponseAPI<String>> deleteCourse(@PathVariable String courseId) {
        try{
            DeleteCourseRequest deleteCourseRequest = new DeleteCourseRequest();
            deleteCourseRequest.setId(courseId);
            courseService.deleteCourse(deleteCourseRequest);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete course successfully")
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
    public ResponseEntity<ResponseAPI<GetCoursesResponse>> getCourses(@RequestParam(name="name",required = false,defaultValue = "") String search,
                                                                      @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                      @RequestParam(name="size",required = false,defaultValue = "10") int size) {
        try{
            GetCoursesResponse resData = courseService.getCourses( page-1, size,search);
            ResponseAPI<GetCoursesResponse> res = ResponseAPI.<GetCoursesResponse>builder()
                    .timestamp(new Date())
                    .message("Get course successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetCoursesResponse> res = ResponseAPI.<GetCoursesResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
}
