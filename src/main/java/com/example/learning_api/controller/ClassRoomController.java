package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;

import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.response.classroom.CreateClassRoomResponse;
import com.example.learning_api.dto.response.classroom.GetClassRoomsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IClassRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(CLASSROOM_BASE_PATH)
@Slf4j
public class ClassRoomController {
    private final IClassRoomService classRoomService;
    private final JwtService jwtService;
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetClassRoomsResponse>> getClassRoom(@RequestParam(name="name",required = false,defaultValue = "") String search,
                                                            @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                            @RequestParam(name="size",required = false,defaultValue = "10") int size) {
        try{
            GetClassRoomsResponse resData = classRoomService.getClassRooms( page-1, size,search);
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
}
