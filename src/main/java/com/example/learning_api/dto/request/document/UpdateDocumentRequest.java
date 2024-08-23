package com.example.learning_api.dto.request.document;


import lombok.Data;

@Data
public class UpdateDocumentRequest {
    private String id;
    private String name;
    private String description;
}
