package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.admin.ChangeRoleRequest;
import com.example.learning_api.dto.response.admin.GetAdminDashboardResponse;
import com.example.learning_api.dto.response.admin.GetUsersResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(ADMIN_BASE_PATH)
public class AdminController {
    private final IAdminService adminService;

    @PostMapping(path = "/change-role")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> changeRole(@RequestBody @Valid ChangeRoleRequest body) {
        try{
            adminService.changeRole(body);
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

    @DeleteMapping(path = "/block-account/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> blockAccount(@PathVariable  String userId ) {
        try{
            adminService.blockAccount(userId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Block account successfully")
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

    @DeleteMapping(path = "/delete-account/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<String>> deleteAccount(@PathVariable  String userId ) {
        try{
            adminService.deleteAccount(userId);
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

    @GetMapping(path = "/dashboard")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseAPI<GetAdminDashboardResponse>> getAdminDashboard() {
        try{
            GetAdminDashboardResponse data = adminService.getAdminDashboard();
            ResponseAPI<GetAdminDashboardResponse> res = ResponseAPI.<GetAdminDashboardResponse>builder()
                    .timestamp(new Date())
                    .message("Get admin dashboard successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetAdminDashboardResponse> res = ResponseAPI.<GetAdminDashboardResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @GetMapping(path = "/teacher")
    public ResponseEntity<ResponseAPI<GetUsersResponse>> getTeacher(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                                                   @RequestParam(value = "search", defaultValue = "") String search,
                                                                    @RequestParam(value = "status", defaultValue = "") String status,
                                                                    @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                    @RequestParam(value = "order", defaultValue = "createdAt") String order) {
        try{
            GetUsersResponse data = adminService.getTeachers(search, page, size, order, sort, status);
            ResponseAPI<GetUsersResponse> res = ResponseAPI.<GetUsersResponse>builder()
                    .timestamp(new Date())
                    .message("Get teacher successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetUsersResponse> res = ResponseAPI.<GetUsersResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/student")
    public ResponseEntity<ResponseAPI<GetUsersResponse>> getStudent(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                                                   @RequestParam(value = "search", defaultValue = "") String search,
                                                                    @RequestParam(value = "status", defaultValue = "") String status,
                                                                    @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                    @RequestParam(value = "order", defaultValue = "createdAt") String order) {
        try{
            GetUsersResponse data = adminService.getStudents(search, page, size, order, sort, status);
            ResponseAPI<GetUsersResponse> res = ResponseAPI.<GetUsersResponse>builder()
                    .timestamp(new Date())
                    .message("Get student successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetUsersResponse> res = ResponseAPI.<GetUsersResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
}
