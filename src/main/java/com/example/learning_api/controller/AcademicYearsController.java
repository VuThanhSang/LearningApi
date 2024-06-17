package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.entity.sql.database.AcademicYearsEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IAcademicYearsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.example.learning_api.constant.RouterConstant.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(ACADEMIC_YEARS_BASE_PATH)
public class AcademicYearsController {
    private final IAcademicYearsService academicYearsService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> create(@RequestBody @Valid AcademicYearsEntity body) {
        try{
            academicYearsService.createAcademicYear(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("create  successfully")
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
    @PatchMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<String>> update(@PathVariable String id, @RequestBody @Valid AcademicYearsEntity body) {
        try{
            body.setId(id);
            academicYearsService.updateAcademicYear(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Change role successfully")
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

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<String>> delete(@PathVariable String id) {
        try{
            academicYearsService.deleteAcademicYear(id);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete account successfully")
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
    public ResponseEntity<ResponseAPI<List<AcademicYearsEntity>>> getAll() {
        try{
            List<AcademicYearsEntity> data = academicYearsService.getAllAcademicYears();
            ResponseAPI<List<AcademicYearsEntity>> res = ResponseAPI.<List<AcademicYearsEntity>>builder()
                    .timestamp(new Date())
                    .message("Get admin dashboard successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<List<AcademicYearsEntity>> res = ResponseAPI.<List<AcademicYearsEntity>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<AcademicYearsEntity>> getById(@PathVariable String id) {
        try{
            AcademicYearsEntity data = academicYearsService.getAcademicYearById(id);
            ResponseAPI<AcademicYearsEntity> res = ResponseAPI.<AcademicYearsEntity>builder()
                    .timestamp(new Date())
                    .message("Get admin dashboard successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<AcademicYearsEntity> res = ResponseAPI.<AcademicYearsEntity>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }


}
