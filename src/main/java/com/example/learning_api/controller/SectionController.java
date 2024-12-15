package com.example.learning_api.controller;


import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.section.CreateSectionRequest;
import com.example.learning_api.dto.request.section.DeleteSectionRequest;
import com.example.learning_api.dto.request.section.UpdateSectionRequest;
import com.example.learning_api.dto.response.section.CreateSectionResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.ISectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.SECTION_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(SECTION_BASE_PATH)
@Slf4j
public class SectionController {
    private final ISectionService sectionService;
    private final JwtService jwtService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    @PostMapping(path = "")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<CreateSectionResponse>> createSection(@RequestBody @Valid CreateSectionRequest body) {
        try{
            CreateSectionResponse resDate = sectionService.createSection(body);
            ResponseAPI<CreateSectionResponse> res = ResponseAPI.<CreateSectionResponse>builder()
                    .timestamp(new Date())
                    .message("Create section successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateSectionResponse> res = ResponseAPI.<CreateSectionResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @PatchMapping(path = "/{sectionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateSection(@RequestBody @Valid UpdateSectionRequest body, @PathVariable String sectionId) {
        try{
            body.setId(sectionId);
            sectionService.updateSection(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update section successfully")
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
    @DeleteMapping(path = "/{sectionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> deleteSection(@PathVariable String sectionId) {
        try{
            DeleteSectionRequest deleteSectionRequest = new DeleteSectionRequest();
            deleteSectionRequest.setId(sectionId);
            sectionService.deleteSection(deleteSectionRequest);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete section successfully")
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
    public ResponseEntity<ResponseAPI<GetSectionsResponse>> getSections(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String classroomId,
            @RequestHeader(name = "Authorization") String authorizationHeader
    ) {
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String role = jwtService.extractRole(accessToken);
            GetSectionsResponse resData = sectionService.getSections(page-1, size, classroomId,role);
            ResponseAPI<GetSectionsResponse> res = ResponseAPI.<GetSectionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get sections successfully")
                    .data(resData)
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

    @GetMapping(path = "/classroom/{classroomId}")
    public ResponseEntity<ResponseAPI<GetSectionsResponse>> getSectionsByClassRoomId(
            @PathVariable String classroomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(name = "Authorization") String authorizationHeader
    ) {
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String role = jwtService.extractRole(accessToken);
            String userId = jwtService.extractUserId(accessToken);
            String id="";
            if(role.equals("USER")){
                id = studentRepository.findByUserId(userId).getId();
            }
            else if (role.equals("TEACHER")){
                id = teacherRepository.findByUserId(userId).getId();
            }
            GetSectionsResponse resData = sectionService.getSectionsByClassRoomId(classroomId, page-1, size,role,id);
            ResponseAPI<GetSectionsResponse> res = ResponseAPI.<GetSectionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get sections successfully")
                    .data(resData)
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
}
