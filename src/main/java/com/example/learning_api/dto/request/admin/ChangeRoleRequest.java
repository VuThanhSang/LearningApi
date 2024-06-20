package com.example.learning_api.dto.request.admin;

import lombok.Data;

@Data
public class ChangeRoleRequest {
    private String userId;
    private String role;


}
