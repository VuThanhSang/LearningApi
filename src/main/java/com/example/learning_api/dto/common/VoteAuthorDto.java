package com.example.learning_api.dto.common;

import com.example.learning_api.enums.RoleEnum;
import lombok.Data;

import javax.management.relation.Role;

@Data
public class VoteAuthorDto {
    private String authorId;
    private RoleEnum role;
    public VoteAuthorDto(String authorId, RoleEnum role) {
        this.authorId = authorId;
        this.role = role;
    }
}
