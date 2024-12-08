package com.example.learning_api.dto.request.document;

import com.example.learning_api.enums.BlockType;
import lombok.Data;

import java.util.List;

@Data
public class CreateBlocksRequest {
    private String documentId;
    List<String> blocks;
}
