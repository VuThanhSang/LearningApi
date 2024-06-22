package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.entity.sql.database.MajorsEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IMajorsService;
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
@RequestMapping(MAJORS_BASE_PATH)
public class MajorsController {
    private final IMajorsService majorsService;

    @PostMapping(path = "")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> create(@RequestBody @Valid MajorsEntity body) {
        try{
            majorsService.createMajor(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create Majors successfully")
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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> update(@PathVariable String id, @RequestBody @Valid MajorsEntity body) {
        try{
            body.setId(id);
            majorsService.updateMajor(body);
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> delete(@PathVariable String id) {
        try{
            majorsService.deleteMajor(id);
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

    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<List<MajorsEntity>>> getAll() {
        try{
            List<MajorsEntity> data = majorsService.getAllMajors();
            ResponseAPI<List<MajorsEntity>> res = ResponseAPI.<List<MajorsEntity>>builder()
                    .timestamp(new Date())
                    .message("Get all majors successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<List<MajorsEntity>> res = ResponseAPI.<List<MajorsEntity>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<MajorsEntity>> getById(@PathVariable String id) {
        try{
            MajorsEntity data = majorsService.getMajorById(id);
            ResponseAPI<MajorsEntity> res = ResponseAPI.<MajorsEntity>builder()
                    .timestamp(new Date())
                    .message("Get major by id successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<MajorsEntity> res = ResponseAPI.<MajorsEntity>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
}
