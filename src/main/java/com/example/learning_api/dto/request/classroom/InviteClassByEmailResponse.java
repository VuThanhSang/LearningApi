package com.example.learning_api.dto.request.classroom;

import lombok.Data;

import java.util.List;

@Data
public class InviteClassByEmailResponse {
    private List<String> success;
    private List<String> fail;
}
