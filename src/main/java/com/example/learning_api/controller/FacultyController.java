package com.example.learning_api.controller;


import com.example.learning_api.dto.request.faculty.CreateFacultyRequest;
import com.example.learning_api.dto.request.faculty.ImportFacultyRequest;
import com.example.learning_api.dto.request.faculty.UpdateFacultyRequest;
import com.example.learning_api.dto.response.faculty.GetFacultiesResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IFacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(FACULTY_BASE_PATH)
public class FacultyController {

    private final IFacultyService facultyService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> createFaculty(@RequestBody @Valid CreateFacultyRequest body) {
        try{
            facultyService.createFaculty(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create faculty successfully")
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

    @PatchMapping(path = "/{facultyId}")
    public ResponseEntity<ResponseAPI<String>> updateFaculty(@RequestBody @Valid UpdateFacultyRequest body, @PathVariable String facultyId) {
        try{
            body.setId(facultyId);
            facultyService.updateFaculty(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update faculty successfully")
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

    @DeleteMapping(path = "/{facultyId}")
    public ResponseEntity<ResponseAPI<String>> deleteFaculty(@PathVariable String facultyId) {
        try{
            facultyService.deleteFaculty(facultyId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete faculty successfully")
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

    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetFacultiesResponse>> getFaculties(@RequestParam String search) {
        try{
           GetFacultiesResponse data=  facultyService.getFaculties(search);
            ResponseAPI<GetFacultiesResponse> res = ResponseAPI.<GetFacultiesResponse>builder()
                    .message("Get faculties successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            log.error(e.getMessage());
            ResponseAPI<GetFacultiesResponse> res = ResponseAPI.<GetFacultiesResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping(path = "/import", consumes = "multipart/form-data")
    public ResponseEntity<ResponseAPI<String>> importFaculty(@ModelAttribute @Valid ImportFacultyRequest body) {
        try{
            facultyService.importFaculty(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Import faculty successfully")
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

}
