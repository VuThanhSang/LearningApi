package com.example.learning_api.dto.response.admin;

import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetUsersResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<UserEntity> data;
}
