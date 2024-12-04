package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;

import com.example.learning_api.dto.request.classroom.ImportClassRoomRequest;
import com.example.learning_api.dto.request.classroom.InviteClassByEmailResponse;
import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.request.faculty.ImportFacultyRequest;
import com.example.learning_api.dto.response.classroom.*;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.dto.response.student.StudentsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IClassRoomService;
import com.example.learning_api.service.core.IStudentEnrollmentsService;
import com.example.learning_api.service.core.IStudentService;
import com.example.learning_api.service.core.ITeacherService;
import com.example.learning_api.service.core.Impl.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(CLASSROOM_BASE_PATH)
@Slf4j
public class ClassRoomController {
    private final IClassRoomService classRoomService;
    private final JwtService jwtService;
    private final IStudentService studentService;
    private final ITeacherService teacherService;
    private final IStudentEnrollmentsService studentEnrollmentsService;
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<CreateClassRoomResponse>> createClassRoom(@ModelAttribute @Valid CreateClassRoomRequest body,@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        try{
            CreateClassRoomResponse resDate = classRoomService.createClassRoom(body);
            ResponseAPI<CreateClassRoomResponse> res = ResponseAPI.<CreateClassRoomResponse>builder()
                    .timestamp(new Date())
                    .message("Create class room successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateClassRoomResponse> res = ResponseAPI.<CreateClassRoomResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @PatchMapping(path = "/{classroomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateClassRoom(@ModelAttribute @Valid UpdateClassRoomRequest body, @PathVariable String classroomId) {
        try{
            body.setId(classroomId);
            classRoomService.updateClassRoom(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update class room successfully")
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

    @GetMapping(path = "/teacher/{teacherId}")
    public ResponseEntity<ResponseAPI<GetClassRoomsResponse>> getClassRoomByTeacherId(
            @RequestParam(name="page",required = false,defaultValue = "1") int page,
            @RequestParam(name="size",required = false,defaultValue = "10") int size,
            @PathVariable(name="teacherId",required = true) String teacherId) {
        try{
            GetClassRoomsResponse resData = classRoomService.getClassRoomsByTeacherId( page-1, size,teacherId);
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message("Get class room by teacherId successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetClassRoomsResponse>> getClassRoom(
            @RequestParam(name="name",required = false,defaultValue = "") String search,
                                                            @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                            @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                @RequestParam(name="status",required = false,defaultValue = "") String status,
                                @RequestHeader(name = "Authorization") String authorizationHeader) {
        try{
            String token = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);
            String callId = "";
            if (role.equals("USER")){
               callId = studentService.getStudentByUserId(userId).getId();
            }
            else if (role.equals("TEACHER")){
                callId = teacherService.getTeacherByUserId(userId).getId();
            }
            else{
                callId = "";
            }
            if (status.equals("")){
                status =null;
            }
            GetClassRoomsResponse resData = classRoomService.getClassRooms( page-1, size,search,callId,role,status);
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message("Get class room successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @DeleteMapping(path = "/{classroomId}")
    @PreAuthorize("hasAnyAuthority('TEACHER,ADMIN')")
    public ResponseEntity<ResponseAPI<String>> deleteClassRoom(@PathVariable String classroomId) {
        try{
            classRoomService.deleteClassRoom(classroomId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete class room successfully")
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

    @GetMapping(path = "/{classroomId}/sections")
    public ResponseEntity<ResponseAPI<GetSectionsResponse>> getSectionsByClassroomId(@PathVariable String classroomId,@RequestHeader(name = "Authorization") String authorizationHeader
                                                                                     ) {
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String role = jwtService.extractRole(accessToken);
            GetSectionsResponse data= classRoomService.getSectionsByClassroomId(0,10,classroomId,role);
            ResponseAPI<GetSectionsResponse> res = ResponseAPI.<GetSectionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get sections by classroomId successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetSectionsResponse> res = ResponseAPI.<GetSectionsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }


    @GetMapping(path = "/schedule-day/{studentId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<GetClassRoomsResponse>> getScheduleByDay(@PathVariable String studentId,
                                                                              @RequestParam(name="day",required = false,defaultValue = "") String day) {
        try{
            GetClassRoomsResponse data= classRoomService.getScheduleByDay(studentId,day);
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message("Get schedule by day successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomsResponse> res = ResponseAPI.<GetClassRoomsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/schedule-week/{studentId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<List<GetScheduleResponse>>> getScheduleByStudentId(@PathVariable String studentId) {
        try{
            List<GetScheduleResponse> data= classRoomService.getScheduleByStudentId(studentId);
            ResponseAPI<List<GetScheduleResponse>> res = ResponseAPI.<List<GetScheduleResponse>>builder()
                    .timestamp(new Date())
                    .message("Get schedule by studentId successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<List<GetScheduleResponse>> res = ResponseAPI.<List<GetScheduleResponse>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/detail/{classroomId}")
    public ResponseEntity<ResponseAPI<GetClassRoomDetailResponse>> getClassRoomDetail(@PathVariable String classroomId,@RequestHeader(name = "Authorization") String authorizationHeader) {
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String role = jwtService.extractRole(accessToken);
            GetClassRoomDetailResponse data= classRoomService.getClassRoomDetail(classroomId,role);
            ResponseAPI<GetClassRoomDetailResponse> res = ResponseAPI.<GetClassRoomDetailResponse>builder()
                    .timestamp(new Date())
                    .message("Get class room detail successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomDetailResponse> res = ResponseAPI.<GetClassRoomDetailResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/invitation/student-not-in-class/{classroomId}")
    public ResponseEntity<ResponseAPI<StudentsResponse>> getStudentInSystem(@RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                            @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                                                            @RequestParam(name="search",required = false,defaultValue = "") String search,
                                                                            @RequestParam(name="sort",required = false,defaultValue = "createdAt") String sortBy,
                                                                            @RequestParam(name="order",required = false,defaultValue = "desc") String sortDirection,
                                                                            @PathVariable String classroomId) {
        try{
            StudentsResponse data= studentEnrollmentsService.getStudents(page-1,size,search,sortBy,sortDirection,classroomId);
            ResponseAPI<StudentsResponse> res = ResponseAPI.<StudentsResponse>builder()
                    .timestamp(new Date())
                    .message("Get student in system successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<StudentsResponse> res = ResponseAPI.<StudentsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @GetMapping(path = "/invitation/{invitationCode}")
    public ResponseEntity<ResponseAPI<GetClassRoomDetailResponse>> getClassRoomByInvitationCode(@PathVariable String invitationCode) {
        try{
            GetClassRoomDetailResponse data= classRoomService.getClassRoomByInvitationCode(invitationCode);
            ResponseAPI<GetClassRoomDetailResponse> res = ResponseAPI.<GetClassRoomDetailResponse>builder()
                    .timestamp(new Date())
                    .message("Get class room by invitation code successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomDetailResponse> res = ResponseAPI.<GetClassRoomDetailResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/invitation/{classroomId}/join")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<String>> joinClassRoom(@PathVariable String classroomId,
                                                             @RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(accessToken);
            String studentId = studentService.getStudentByUserId(userId).getId();
            classRoomService.joinClassRoom(classroomId, studentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Join class room successfully")
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

    @GetMapping(path = "/invitation/{classroomId}/view-request")
    public ResponseEntity<ResponseAPI<GetJoinClassResponse>> getJoinClass(@PathVariable String classroomId,
                                                                          @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                          @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                                                          @RequestParam(name="email", required = false) String email,
                                                                          @RequestParam(name="name", required = false) String name,
                                                                          @RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(accessToken);
            String teacherId = teacherService.getTeacherByUserId(userId).getId();
            GetJoinClassResponse data = classRoomService.getJoinClassRequests( page - 1, size, classroomId, teacherId,email,name);
            ResponseAPI<GetJoinClassResponse> res = ResponseAPI.<GetJoinClassResponse>builder()
                    .timestamp(new Date())
                    .message("Get join class requests successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<GetJoinClassResponse> res = ResponseAPI.<GetJoinClassResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/invitation/{classroomId}/accept-request/{studentId}")
    @PreAuthorize("hasAnyAuthority('TEACHER,ADMIN')")
    public ResponseEntity<ResponseAPI<String>> acceptJoinClass(@PathVariable String classroomId,
                                                               @PathVariable String studentId) {
        try {
            classRoomService.acceptJoinClass(classroomId, studentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Accept join class successfully")
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

    @PostMapping(path = "/invitation/{classroomId}/reject-request/{studentId}")
    @PreAuthorize("hasAnyAuthority('TEACHER,ADMIN')")
    public ResponseEntity<ResponseAPI<String>> rejectJoinClass(@PathVariable String classroomId,
                                                               @PathVariable String studentId) {
        try {
            classRoomService.rejectJoinClass(classroomId, studentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Reject join class successfully")
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

    @PostMapping(path = "/{classroomId}/remove-student/{studentId}")
    @PreAuthorize("hasAnyAuthority('TEACHER,ADMIN')")
    public ResponseEntity<ResponseAPI<String>> removeStudentFromClass(@PathVariable String classroomId,
                                                                     @PathVariable String studentId,
                                                                     @RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(accessToken);
            String teacherId = teacherService.getTeacherByUserId(userId).getId();
            classRoomService.removeStudentFromClass(classroomId, studentId, teacherId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Remove student from class successfully")
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

    @PostMapping(path = "/invite",  consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('TEACHER,ADMIN')")
    public ResponseEntity<ResponseAPI<InviteClassByEmailResponse>> inviteStudentByEmail(@ModelAttribute @Valid InviteStudentByEmailRequest body, @RequestHeader(name = "Authorization") String authorizationHeader){
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(accessToken);
            body.setTeacherId(teacherService.getTeacherByUserId(userId).getId());
            InviteClassByEmailResponse resData = classRoomService.inviteStudentByEmail(body);
            ResponseAPI<InviteClassByEmailResponse> res = ResponseAPI.<InviteClassByEmailResponse>builder()
                    .message("Invite students successfully")
                    .data(resData)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            log.error(e.getMessage());
            ResponseAPI<InviteClassByEmailResponse> res = ResponseAPI.<InviteClassByEmailResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }


    @PostMapping(path = "/import", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<ResponseAPI<String>> importClass(@ModelAttribute @Valid ImportClassRoomRequest body) {
        try{
            classRoomService.importClassRoom(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Import classrooms successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            log.error(e.getMessage());
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/recent")
    public ResponseEntity<ResponseAPI<GetClassRoomRecentResponse>> getRecentClassRoomsByTeacherId(@RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                                       @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                                                                       @RequestHeader(name = "Authorization") String authorizationHeader) {
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(accessToken);
            String role = jwtService.extractRole(accessToken);
            String callId = "";
            if (role.equals("USER")){
                callId = studentService.getStudentByUserId(userId).getId();
            }
            else if (role.equals("TEACHER")){
                callId = teacherService.getTeacherByUserId(userId).getId();
            }
            else{
                throw new IllegalArgumentException("Admin can not get recent class room");
            }
            GetClassRoomRecentResponse resData = classRoomService.getRecentClassesByTeacherId( page-1, size,callId,role);
            ResponseAPI<GetClassRoomRecentResponse> res = ResponseAPI.<GetClassRoomRecentResponse>builder()
                    .timestamp(new Date())
                    .message("Get recent class room by teacherId successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetClassRoomRecentResponse> res = ResponseAPI.<GetClassRoomRecentResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }


    @GetMapping(path = "/{classroomId}/students")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<ResponseAPI<GetStudentInClassResponse>> getStudentsByClassroomId(@PathVariable String classroomId,
                                                                                     @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                                     @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                                                                        @RequestParam(name="search",required = false,defaultValue = "") String search,
                                                                                        @RequestParam(name="sort",required = false,defaultValue = "createdAt") String sortBy,
                                                                                        @RequestParam(name="sortDirection",required = false,defaultValue = "desc") String sortDirection)
                                                                                      {
        try {
            GetStudentInClassResponse data = studentEnrollmentsService.getStudentInClass(classroomId, page - 1, size,search,sortBy,sortDirection);
            ResponseAPI<GetStudentInClassResponse> res = ResponseAPI.<GetStudentInClassResponse>builder()
                    .timestamp(new Date())
                    .message("Get students by classroomId successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<GetStudentInClassResponse> res = ResponseAPI.<GetStudentInClassResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


    @GetMapping(path = "/{classroomId}/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<ResponseAPI<GetDetailStudentInClassResponse>> removeStudentFromClass(@PathVariable String classroomId,
                                                                      @PathVariable String studentId) {
        try {
            GetDetailStudentInClassResponse data = classRoomService.getDetailStudentInClass(classroomId, studentId);
            ResponseAPI<GetDetailStudentInClassResponse> res = ResponseAPI.<GetDetailStudentInClassResponse>builder()
                    .timestamp(new Date())
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<GetDetailStudentInClassResponse> res = ResponseAPI.<GetDetailStudentInClassResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }



}
