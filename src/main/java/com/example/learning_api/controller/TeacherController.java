package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.deadline.GetUpcomingDeadlineResponse;
import com.example.learning_api.dto.request.teacher.CreateTeacherRequest;
import com.example.learning_api.dto.request.teacher.UpdateTeacherRequest;
import com.example.learning_api.dto.response.cart.GetPaymentForTeacher;
import com.example.learning_api.dto.response.teacher.CreateTeacherResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;
import com.example.learning_api.dto.response.teacher.TeacherDashboardResponse;
import com.example.learning_api.dto.response.test.GetTestInProgress;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.ITeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(TEACHER_BASE_PATH)
@Slf4j
public class TeacherController {
    private final ITeacherService teacherService;
    private final JwtService jwtService;
    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<CreateTeacherResponse>> createTeacher(@RequestBody @Valid CreateTeacherRequest body) {
        try{
            CreateTeacherResponse resDate = teacherService.createTeacher(body);
            ResponseAPI<CreateTeacherResponse> res = ResponseAPI.<CreateTeacherResponse>builder()
                    .timestamp(new Date())
                    .message("Create teacher successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateTeacherResponse> res = ResponseAPI.<CreateTeacherResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @PatchMapping(path = "/{teacherId}")
    public ResponseEntity<ResponseAPI<String>> updateTeacher(@RequestBody @Valid UpdateTeacherRequest body, @PathVariable String teacherId) {
        try{
            body.setId(teacherId);
            teacherService.updateTeacher(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update teacher successfully")
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
    @DeleteMapping(path = "/{teacherId}")
    public ResponseEntity<ResponseAPI<String>> deleteTeacher(@PathVariable String teacherId) {
        try{
            teacherService.deleteTeacher(teacherId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete teacher successfully")
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
    public ResponseEntity<ResponseAPI<GetTeachersResponse>> getTeacher(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        try{
            GetTeachersResponse getTeachersResponse =  teacherService.getTeachers(page-1, size, search);
            ResponseAPI<GetTeachersResponse> res = ResponseAPI.<GetTeachersResponse>builder()
                    .timestamp(new Date())
                    .message("Get teacher successfully")
                    .data(getTeachersResponse)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetTeachersResponse> res = ResponseAPI.<GetTeachersResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @PostMapping(path = "/{teacherId}/subject-specialization/{majorId}")
    public ResponseEntity<ResponseAPI<String>> addSubjectSpecialization(@PathVariable String teacherId, @PathVariable String majorId) {
        try{
            teacherService.addSubjectSpecialization(teacherId, majorId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Add subject specialization successfully")
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


    @GetMapping(path = "/dashboard")
    public ResponseEntity<ResponseAPI<TeacherDashboardResponse>> getTeacherDashboard(@RequestHeader("Authorization") String authorization) {
        try{
            String token = authorization.substring(7);
            String userId = jwtService.extractUserId(token);
            String teacherId = teacherService.getTeacherByUserId(userId).getId();
            TeacherDashboardResponse getTestInProgress =  teacherService.getTeacherDashboard(teacherId);
            ResponseAPI<TeacherDashboardResponse> res = ResponseAPI.<TeacherDashboardResponse>builder()
                    .timestamp(new Date())
                    .message("Get teacher dashboard successfully")
                    .data(getTestInProgress)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<TeacherDashboardResponse> res = ResponseAPI.<TeacherDashboardResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/transaction")
    public ResponseEntity<ResponseAPI<GetPaymentForTeacher>> getPaymentForTeacher(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try{
            String token = authorization.substring(7);
            String userId = jwtService.extractUserId(token);
            String teacherId = teacherService.getTeacherByUserId(userId).getId();
            GetPaymentForTeacher getPaymentForTeacher =  teacherService.getPaymentForTeacher(teacherId, page-1, size);
            ResponseAPI<GetPaymentForTeacher> res = ResponseAPI.<GetPaymentForTeacher>builder()
                    .timestamp(new Date())
                    .message("Get payment for teacher successfully")
                    .data(getPaymentForTeacher)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetPaymentForTeacher> res = ResponseAPI.<GetPaymentForTeacher>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

}
