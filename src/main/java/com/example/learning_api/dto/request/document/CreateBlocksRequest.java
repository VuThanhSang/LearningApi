package com.example.learning_api.dto.request.document;

import com.example.learning_api.enums.BlockType;
import lombok.Data;

import java.util.List;

@Data
public class CreateBlocksRequest {
    private String documentId;
    List<Block> blocks;
    @Data
    public static class Block {
        private String content;
        private BlockType type;
    }
}
