package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.response.classroom.GetApprovalClassroomResponse;
import com.example.learning_api.entity.sql.database.CategoryEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/category")
public class CategoryController {
   private final  IAdminService adminService;
    @PostMapping(path = "/")
    public ResponseEntity<ResponseAPI<String>> createCategory(@RequestBody @Valid CategoryEntity body) {
        try{
            adminService.createCategory(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create category successfully")
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
    public ResponseEntity<ResponseAPI<String>> updateCategory(@PathVariable String id, @RequestBody @Valid CategoryEntity body) {
        try{
            adminService.updateCategory(id, body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update category successfully")
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
    public ResponseEntity<ResponseAPI<String>> deleteCategory(@PathVariable String id) {
        try{
            adminService.deleteCategory(id);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete category successfully")
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
    public ResponseEntity<ResponseAPI<List<CategoryEntity>>> getAllCategory(@RequestParam(name="search",required = false,defaultValue = "") String search) {
        try{
            List<CategoryEntity> data= adminService.getCategories(search);
            ResponseAPI<List<CategoryEntity>> res = ResponseAPI.<List<CategoryEntity>>builder()
                    .timestamp(new Date())
                    .message("Get all category successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<List<CategoryEntity>> res = ResponseAPI.<List<CategoryEntity>>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }



}
